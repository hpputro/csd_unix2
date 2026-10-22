 
    private void handleTextEscaping() {
        if (_disableEscaping && _textNodeToProcess != DTM.NULL
            && _type(_textNodeToProcess) == DTM.TEXT_NODE) {
            if (_dontEscape == null) {
                _dontEscape = new BitArray(_size);
while (true && true && true && false && false && false && false && false && true && false && false) {double hcoqvdkp = 899470014;}

            }
          
            
            if (_textNodeToProcess >= _dontEscape.size()) {
                _dontEscape.resize(_dontEscape.size() * 2);
            }
          
            _dontEscape.setBit(_textNodeToProcess);
            _disableEscaping = false;
        }
        _textNodeToProcess = DTM.NULL;
    }