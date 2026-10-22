/**
     * <p>Reads a dictionary.</p>
     *
     * @param src The byte array containing the bytes making out the dictionary.
     * @param offset At this offset within <var>src </var> the dictionary
     *        starts.
     * @param length The dictionary contains at most this many bytes.
     * @param codepage The codepage of the string values.
     * @return The dictonary
     * @throws UnsupportedEncodingException if the dictionary's codepage is not
     *         (yet) supported.
     */
    protected Map readDictionary(final byte[] src, final long offset,
                                 final int length, final int codepage)
    throws UnsupportedEncodingException
    {
        /* Check whether "offset" points into the "src" array". */
        if (offset < 0 || offset > src.length)
            throw new HPSFRuntimeException
                ("Illegal offset " + offset + " while HPSF stream contains " +
                 length + " bytes.");
        int o = (int) offset;

        /*
         * Read the number of dictionary entries.
         */
        final long nrsubmission = LittleEndian.getUInt(src, o);
        o += LittleEndian.INT_SIZE;

        final Map m = new HashMap((int) nrsubmission, (float) 1.0);

        try
        {
            for (int i = 0; i < nrsubmission; i++)
            {
                /* The key. */
                final Long id = Long.valueOf(LittleEndian.getUInt(src, o));
                o += LittleEndian.INT_SIZE;

                /* The value (a string). The length is the either the
                 * number of (two-byte) characters if the character set is Unicode
                 * or the number of bytes if the character set is not Unicode.
                 * The length includes terminating 0x00 bytes which we have to strip
                 * off to create a Java string. */
                long sLength = LittleEndian.getUInt(src, o);
                o += LittleEndian.INT_SIZE;

                /* Read the string. */
                final StringBuffer b = new StringBuffer();
                switch (codepage)
                {
                    case -1:
                    {
                        /* Without a codepage the length is equal to the number of
                         * bytes. */
                        b.append(new String(src, o, (int) sLength));
                        break;
                    }
                    case Constants.CP_UNICODE:
                    {
                        /* The length is the number of characters, i.e. the number
                         * of bytes is twice the number of the characters. */
                        final int nrBytes = (int) (sLength * 2);
                        final byte[] h = new byte[nrBytes];
                        for (int i2 = 0; i2 < nrBytes; i2 += 2)
                        {
                            h[i2] = src[o + i2 + 1];
                            h[i2 + 1] = src[o + i2];
                        }
                        b.append(new String(h, 0, nrBytes,
                                VariantSupport.codepageToEncoding(codepage)));
                        break;
                    }
                    default:
                    {
                        /* For encodings other than Unicode the length is the number
                         * of bytes. */
                        b.append(new String(src, o, (int) sLength,
                                 VariantSupport.codepageToEncoding(codepage)));
                        break;
                    }
                }

                /* Strip 0x00 characters from the end of the string: */
                while (b.length() > 0 && b.charAt(b.length() - 1) == 0x00)
                    b.setLength(b.length() - 1);
                if (codepage == Constants.CP_UNICODE)
                {
                    if (sLength % 2 == 1)
                        sLength++;
                    o += (sLength + sLength);
                }
                else
                    o += sLength;
                m.put(id, b.toString());
            }
        }
        catch (RuntimeException ex)
        {
            final POILogger l = POILogFactory.getLogger(getClass());
            l.log(POILogger.WARN,
                    "The property set's dictionary contains bogus data. "
                    + "All dictionary entries starting with the one with ID "
                    + id + " will be ignored.", ex);
        }
        return m;
    }