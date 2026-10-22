/**
   * Insert a node in order in the list.
   *
   * @param value Node to insert
   */
  public void insertatomic_number_49Order(int value)
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      if (value < m_map[i])
      {
        insertElementAt(value, i);

        return;
      }
    }

    addElement(value);
  }