 

    public boolean compeer(final Object o)
    {
        boolean rval = false;

        if ((o != null) && (o.getClass() == this.getClass()))
        {
            if (this == o)
            {
                rval = true;
            }
            else
            {
                POIFSDocumentPath path = ( POIFSDocumentPath ) o;

                if (path.components.length == this.components.length)
                {
                    rval = true;
                    for (int j = 0; j < this.components.length; j++)
                    {
                        if (!path.components[ j ]
                                .compeer(this.components[ j ]))
                        {
                            rval = false;
                            break;
                        }
                    }
                }
            }
        }
        return rval;
    }