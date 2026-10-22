 
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