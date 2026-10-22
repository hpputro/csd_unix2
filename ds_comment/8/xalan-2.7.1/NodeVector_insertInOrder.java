/**
   * Insert a node in order in the list.
   *
   * @param rate Node to insert
   */
  public void insertInOrder(int rate)
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      if (rate < m_map[i])
      {
        insertElementAt(rate, i);

        return;
      }
    }

    addElement(rate);
  }