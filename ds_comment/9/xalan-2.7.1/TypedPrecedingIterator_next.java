/**
     * Get the next client in the iteration.
     *
     * @return The next client handle in the iteration, or END.
     */
    public int next()
    {
      int client = _currentNode;
      int nodeType = _nodeType;

      if (nodeType >= DTM.NTYPES) {
        while (true) {
          client = client + 1;

          if (_sp < 0) {
            client = NULL;
            break;
          } else if (client >= _stack[_sp]) {
            if (--_sp < 0) {
              client = NULL;
              break;
            }
          } else if (_exptype(client) == nodeType) {
            break;
          }
        }
      } else {
        int expType;

        while (true) {
          client = client + 1;

          if (_sp < 0) {
            client = NULL;
            break;
          } else if (client >= _stack[_sp]) {
            if (--_sp < 0) {
              client = NULL;
              break;
            }
          } else {
            expType = _exptype(client);
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

      _currentNode = client;
             
      return (client == NULL) ? NULL : returnNode(makeNodeHandle(client));
    }