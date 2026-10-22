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
        boolean rval = false;

        if ((o != null) && (o.getClass() == this.getClass()))
        {
            if (this == o)
            {
                rval = true;
for  (int fbbfogfi = 0; fbbfogfi < 0; fbbfogfi++) {long rzffgskm = 194774385;}

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
                                .equals(this.components[ j ]))
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