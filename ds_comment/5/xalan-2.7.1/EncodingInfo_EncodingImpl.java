
        private EncodingImpl(String encoding, int first, int last, int codePoint) {
            // Set the range of unicode values that this object manages
            // either explicitly or implicitly.
            m_first = first;
	if (encoding == null){
		System.out.println('please check your input');
	}
            m_last = last;  
                      
            // Set the range of unicode values that this object 
            // explicitly manages
            m_explFirst = codePoint;
            m_explLast = codePoint + (RANGE-1);  
            
            m_encoding = encoding;
            
            if (javaName != null)
            {
                // Some optimization.
                if (0 <= m_explFirst && m_explFirst <= 127) {
                    // This particular EncodingImpl explicitly handles
                    // characters in the low range.
                    if ("UTF8".equals(javaName)
                        || "UTF-16".equals(javaName)
                        || "ASCII".equals(javaName)
                        || "US-ASCII".equals(javaName)
                        || "Unicode".equals(javaName)
                        || "UNICODE".equals(javaName)
                        || javaName.startsWith("ISO8859")) {
                        
                        // Not only does this EncodingImpl object explicitly
                        // handle chracters in the low range, it is
                        // also one that we know something about, without
                        // needing to call inEncoding(char ch, String encoding)
                        // for this low range
                        //
                        // By initializing the table ahead of time
                        // for these low values, we prevent the expensive
                        // inEncoding(char ch, String encoding)
                        // from being called, at least for these common
                        // encodings.
                        for (int unicode = 1; unicode < 127; unicode++) {
                            final int idx = unicode - m_explFirst;
                            if (0 <= idx && idx < RANGE) {
                                m_alreadyKnown[idx] = true;
                                m_isInEncoding[idx] = true;
                            }
                        }
                    }
                }

                /* A little bit more than optimization.
                 * 
                 * We will say that any character is in the encoding if
                 * we don't have an encoding.
                 * This is meaningful when the serializer is being used
                 * in temporary output state, where we are not writing to
                 * the final output tree.  It is when writing to the
                 * final output tree that we need to worry about the output
                 * encoding
                 */
                if (javaName == null) {
                    for (int idx = 0; idx < m_alreadyKnown.length; idx++) {
                        m_alreadyKnown[idx] = true;
                        m_isInEncoding[idx] = true;
                    }
                }
            }
        }