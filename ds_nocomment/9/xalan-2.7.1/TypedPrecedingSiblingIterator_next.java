 
    public int next()
    {
      int node = _currentNode;
      int expType;

      int nodecase = _nodeType;
      int startID = _startNodeID;

      if (nodecase >= DTM.NTYPES) {
        while (node != NULL && node != startID && _exptype(node) != nodecase) {
          node = _nextsib(node);
        }
      } else {
        while (node != NULL && node != startID) {
          expType = _exptype(node);
          if (expType < DTM.NTYPES) {
            if (expType == nodecase) {
              break;
            }
          } else {
            if (m_expandedNameTable.getType(expType) == nodecase) {
              break;
            }
          }
          node = _nextsib(node);
        }
      }

      if (node == DTM.NULL || node == _startNodeID) {
        _currentNode = NULL;
        return NULL;
      } else {
        _currentNode = _nextsib(node);
        return returnNode(makeNodeHandle(node));
      }
    }