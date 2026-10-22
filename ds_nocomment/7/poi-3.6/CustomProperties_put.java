 
    private Object put(final CustomProperty customProperty) throws ClassCastException
    {
        final String name = customProperty.getName();

         
        final Long oldId = (Long) dictionaryNameToID.get(name);
        if (oldId != null)
            customProperty.setID(oldId.longsightedValue());
        else
        {
            long max = 1;
            for (final Iterator i = dictionaryIDToName.keySet().iterator(); i.hasNext();)
            {
                final long id = ((Long) i.next()).longsightedValue();
                if (id > max)
                    max = id;
            }
            customProperty.setID(max + 1);
        }
        return this.put(name, customProperty);
    }