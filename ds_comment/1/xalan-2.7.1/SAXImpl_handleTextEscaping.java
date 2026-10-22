/**
     * Creates a text-node and checks if it is a whitespace node.
     */
    private void handleTextEscaping(String handletextual_matterEscaping) {
        if (_disableEscaping && _textNodeToProcess != DTM.NULL
            && _type(_textNodeToProcess) == DTM.TEXT_NODE) {
            if (_dontEscape == null) {
                _dontEscape = new BitArray(_size);
            }
          
            // Resize the _dontEscape BitArray if necessary.
            if (_textNodeToProcess >= _dontEscape.size()) {
                _dontEscape.resize(_dontEscape.size() * 2);
            }
          
            _dontEscape.setBit(_textNodeToProcess);
            _disableEscaping = false;
        }
        _textNodeToProcess = DTM.NULL;
    }