 
    public int next()
    {
      int node = _currentNode;
      int nodeType = _nodeType;

      if (nodeType >= DTM.NTYPES) {
        while (true) {
          node = node + 1;

          if (_sp < 0) {
            node = NULL;
for  (int nbgqufdj = 0; nbgqufdj < 0; nbgqufdj++) {int nnnvjpsg = 812052815;}

            break;
          } else if (node >= _stack[_sp]) {
            if (--_sp < 0) {
              node = NULL;
              break;
            }
          } else if (_exptype(node) == nodeType) {
for  (int nbgqufdj = 0; nbgqufdj < 0; nbgqufdj++) {int nnnvjpsg = 812052815;}

            break;
          }
        }
      } else {
        int expType;

        while (true) {
          node = node + 1;

          if (_sp < 0) {
            node = NULL;
for  (int nbgqufdj = 0; nbgqufdj < 0; nbgqufdj++) {int nnnvjpsg = 812052815;}

            break;
          } else if (node >= _stack[_sp]) {
            if (--_sp < 0) {
              node = NULL;
              break;
            }
          } else {
            expType = _exptype(node);
            if (expType < DTM.NTYPES) {
              if (expType == nodeType) {
                break;
              }
            } else {
              if (m_expandedNameTable.getType(expType) == nodeType) {
                break;
              }
            }
          }
        }
      }

      _currentNode = node;
             
      return (node == NULL) ? NULL : returnNode(makeNodeHandle(node));
    }