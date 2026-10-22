

    public HyperlinkRecord(RecordInputStream i) {
        _range = new CellRangeAddress(i);

        _guid = new GUID(i);

         
        int streamVersion = i.readInt();
        if (streamVersion != 0x00000002) {
            throw new RecordFormatException("Stream Version must be 0x2 but found " + streamVersion);
        }
        _linkOpts = i.readInt();

        if ((_linkOpts & HLINK_LABEL) != 0){
            int label_len = i.readInt();
            _label = i.readUnicodeLEString(label_len);
        }

        if ((_linkOpts & HLINK_TARGET_FRAME) != 0){
            int len = i.readInt();
            _targetFrame = i.readUnicodeLEString(len);
        }

        if ((_linkOpts & HLINK_URL) != 0 && (_linkOpts & HLINK_UNC_PATH) != 0) {
            _moniker = null;
            int nChars = i.readInt();
            _address = i.readUnicodeLEString(nChars);
        }

        if ((_linkOpts & HLINK_URL) != 0 && (_linkOpts & HLINK_UNC_PATH) == 0) {
            _moniker = new GUID(i);

            if(URL_MONIKER.equals(_moniker)){
                int length = i.readInt();
                 
                int remaining = i.remaining();
                if (length == remaining) {
                    int nChars = length/2;
                    _address = i.readUnicodeLEString(nChars);
                } else {
                    int nChars = (length - TAIL_SIZE)/2;
                    _address = i.readUnicodeLEString(nChars);
                     
                    _uninterpretedTail = readTail(URL_TAIL, i);
                }
            } else if (FILE_MONIKER.equals(_moniker)) {
                _fileOpts = i.readShort();

                int len = i.readInt();
                _shortFilename = StringUtil.readCompressedUnicode(i, len);
                _uninterpretedTail = readTail(FILE_TAIL, i);
                int size = i.readInt();
                if (size > 0) {
                    int charDataSize = i.readInt();

                    
                    int optFlags = i.readUShort();
                    if (optFlags != 0x0003) {
                        throw new RecordFormatException("Expected 0x3 but found " + optFlags);
                    }
                    _address = StringUtil.readUnicodeLE(i, charDataSize/2);
                } else {
                    _address = null;
                }
            } else if (STD_MONIKER.equals(_moniker)) {
                _fileOpts = i.readShort();

                int len = i.readInt();

                byte[] path_bytes = new byte[len];
                i.readFully(path_bytes);

                _address = new String(path_bytes);
            }
        }

        if((_linkOpts & HLINK_PLACE) != 0) {

            int len = i.readInt();
            _textMark = i.readUnicodeLEString(len);
        }

        if (i.remaining() > 0) {
            System.out.println(HexDump.toHex(i.readRemainder()));
        }
    }