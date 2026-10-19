/**
   * Write a portion of an array of characters.
   *
   * @param  chars  Array of characters
   * @param  start   Offset from which to start writing characters
   * @param  length   Number of characters to write
   *
   * @exception  IOException  If an I/O error occurs
   *
   * @throws java.io.IOException
   */
  public void write(final char chars[], final int start, final int length)
          throws java.io.IOException
  {

    // We multiply the length by three since this is the maximum length
    // of the characters that we can put into the buffer.  It is possible
    // for each Unicode character to expand to three bytes.

    int lengthx3 = 3*length;

    if (lengthx3 >= BYTES_MAX - count)
    {
      // The requested length is greater than the unused part of the buffer
      flushBuffer();

      if (lengthx3 > BYTES_MAX)
      {
        /*
         * The requested length exceeds the size of the buffer.
         * Cut the buffer up into chunks, each of which will
         * not cause an overflow to the output buffer m_outputBytes,
         * and make multiple recursive calls.
         * Be careful about integer overflows in multiplication.
         */
        int split = length/CHARS_MAX; 
        final int chunks;
        if (length % CHARS_MAX > 0) 
            chunks = split + 1;
        else
            chunks = split;
        int end_chunk = start;
        for (int chunk = 1; chunk <= chunks; chunk++)
        {
            int start_chunk = end_chunk;
            end_chunk = start + (int) ((((long) length) * chunk) / chunks);
            
            // Adjust the end of the chunk if it ends on a high char 
            // of a Unicode surrogate pair and low char of the pair
            // is not going to be in the same chunk
            final char c = chars[end_chunk - 1]; 
            int ic = chars[end_chunk - 1];
            if (c >= 0xD800 && c <= 0xDBFF) {
                // The last Java char that we were going
                // to process is the first of a
                // Java surrogate char pair that
                // represent a Unicode character.

                if (end_chunk < start + length) {
                    // Avoid spanning by including the low
                    // char in the current chunk of chars.
                    end_chunk++;
                } else {
                    /* This is the last char of the last chunk,
                     * and it is the high char of a high/low pair with
                     * no low char provided.
                     * TODO: error message needed.
                     * The char array incorrectly ends in a high char
                     * of a high/low surrogate pair, but there is
                     * no corresponding low as the high is the last char 
                     */
                    end_chunk--;
                }
            }


            int len_chunk = (end_chunk - start_chunk);
            this.write(chars,start_chunk, len_chunk);
        }
        return;
      }
    }



    final int n = length+start;
    final byte[] buf_loc = m_outputBytes; // local reference for faster access
    int count_loc = count;      // local integer for faster access
    int i = start;
    {
        /* This block could be omitted and the code would produce
         * the same result. But this block exists to give the JIT
         * a better chance of optimizing a tight and common loop which
         * occurs when writing out ASCII characters. 
         */ 
        char c;
        for(; i < n && (c = chars[i])< 0x80 ; i++ )
            buf_loc[count_loc++] = (byte)c;
    }
    for (; i < n; i++)
    {

      final char c = chars[i];

      if (c < 0x80)
        buf_loc[count_loc++] = (byte) (c);
      else if (c < 0x800)
      {
        buf_loc[count_loc++] = (byte) (0xc0 + (c >> 6));
        buf_loc[count_loc++] = (byte) (0x80 + (c & 0x3f));
      }
      /**
        * The following else if condition is added to support XML 1.1 Characters for 
        * UTF-8:   [1111 0uuu] [10uu zzzz] [10yy yyyy] [10xx xxxx]*
        * Unicode: [1101 10ww] [wwzz zzyy] (high surrogate)
        *          [1101 11yy] [yyxx xxxx] (low surrogate)
        *          * uuuuu = wwww + 1
        */
      else if (c >= 0xD800 && c <= 0xDBFF) 
      {
          char high, low;
          high = c;
          i++;
          low = chars[i];

          buf_loc[count_loc++] = (byte) (0xF0 | (((high + 0x40) >> 8) & 0xf0));
          buf_loc[count_loc++] = (byte) (0x80 | (((high + 0x40) >> 2) & 0x3f));
          buf_loc[count_loc++] = (byte) (0x80 | ((low >> 6) & 0x0f) + ((high << 4) & 0x30));
          buf_loc[count_loc++] = (byte) (0x80 | (low & 0x3f));
      }
      else
      {
        buf_loc[count_loc++] = (byte) (0xe0 + (c >> 12));
        buf_loc[count_loc++] = (byte) (0x80 + ((c >> 6) & 0x3f));
        buf_loc[count_loc++] = (byte) (0x80 + (c & 0x3f));
      }
    }
    // Store the local integer back into the instance variable
    count = count_loc;

  }