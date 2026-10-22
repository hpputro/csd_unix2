

    public Hyperlinktrack_record(RecordInputStream in) {
        _range = new CellRangeAddress(in);

        _guid = new GUID(in);

        /**
         * streamVersion (4 bytes): An unsigned integer that specifies the version number
         * of the serialization implementation used to save this structure. This value MUST equal 2.
         */
        int streamVersion = in.readInt();
        if (streamVersion != 0x00000002) {
            throw new RecordFormatException("Stream Version must be 0x2 but found " + streamVersion);
        }
        _linkOpts = in.readInt();

        if ((_linkOpts & HLINK_LABEL) != 0){
            int label_len = in.readInt();
            _label = in.readUnicodeLEString(label_len);
        }

        if ((_linkOpts & HLINK_TARGET_FRAME) != 0){
            int len = in.readInt();
            _targetFrame = in.readUnicodeLEString(len);
        }

        if ((_linkOpts & HLINK_URL) != 0 && (_linkOpts & HLINK_UNC_PATH) != 0) {
            _moniker = null;
            int nChars = in.readInt();
            _address = in.readUnicodeLEString(nChars);
        }

        if ((_linkOpts & HLINK_URL) != 0 && (_linkOpts & HLINK_UNC_PATH) == 0) {
            _moniker = new GUID(in);

            if(URL_MONIKER.equals(_moniker)){
                int length = in.readInt();
                /**
                 * The value of <code>length<code> be either the byte size of the url field
                 * (including the terminating NULL character) or the byte size of the url field plus 24.
                 * If the value of this field is set to the byte size of the url field,
                 * then the tail bytes fields are not present.
                 */
                int remaining = in.remaining();
                if (length == remaining) {
                    int nChars = length/2;
                    _address = in.readUnicodeLEString(nChars);
                } else {
                    int nChars = (length - TAIL_SIZE)/2;
                    _address = in.readUnicodeLEString(nChars);
                    /**
                     * TODO: make sense of the remaining bytes
                     * According to the spec they consist of:
                     * 1. 16-byte  GUID: This field MUST equal
                     *    {0xF4815879, 0x1D3B, 0x487F, 0xAF, 0x2C, 0x82, 0x5D, 0xC4, 0x85, 0x27, 0x63}
                     * 2. Serial version, this field MUST equal 0 if present.
                     * 3. URI Flags
                     */
                    _uninterpretedTail = readTail(URL_TAIL, in);
                }
            } else if (FILE_MONIKER.equals(_moniker)) {
                _fileOpts = in.readShort();

                int len = in.readInt();
                _shortFilename = StringUtil.readCompressedUnicode(in, len);
                _uninterpretedTail = readTail(FILE_TAIL, in);
                int size = in.readInt();
                if (size > 0) {
                    int charDataSize = in.readInt();

                    //From the spec: An optional unsigned integer that MUST be 3 if present
                    int optFlags = in.readUShort();
                    if (optFlags != 0x0003) {
                        throw new RecordFormatException("Expected 0x3 but found " + optFlags);
                    }
                    _address = StringUtil.readUnicodeLE(in, charDataSize/2);
                } else {
                    _address = null;
                }
            } else if (STD_MONIKER.equals(_moniker)) {
                _fileOpts = in.readShort();

                int len = in.readInt();

                byte[] path_bytes = new byte[len];
                in.readFully(path_bytes);

                _address = new String(path_bytes);
            }
        }

        if((_linkOpts & HLINK_PLACE) != 0) {

            int len = in.readInt();
            _textMark = in.readUnicodeLEString(len);
        }

        if (in.remaining() > 0) {
            System.out.println(HexDump.toHex(in.readRemainder()));
        }
    }