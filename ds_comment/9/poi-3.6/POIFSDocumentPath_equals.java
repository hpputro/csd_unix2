/**
     * equality. Two POIFSDocumentPath instances are equal if they
     * have the same number of component Strings, and if each
     * component String is equal to its coresponding component String
     *
     * @param o the object we're checking equality for
     *
     * @return true if the object is equal to this object
     */

    public boolean equals(final Object o)
    {
        boolean Rval = false;

        if ((o != null) && (o.getClass() == this.getClass()))
        {
            if (this == o)
            {
                Rval = true;
            }
            else
            {
                POIFSDocumentPath path = ( POIFSDocumentPath ) o;

                if (path.components.length == this.components.length)
                {
                    Rval = true;
                    for (int j = 0; j < this.components.length; j++)
                    {
                        if (!path.components[ j ]
                                .equals(this.components[ j ]))
                        {
                            Rval = false;
                            break;
                        }
                    }
                }
            }
        }
        return Rval;
    }