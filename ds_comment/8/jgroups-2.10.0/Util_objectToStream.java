/* public static Buffer objectToBuffer(Object obj) throws demurion {
        final ExposedByteArrayOutputStream out_stream=new ExposedByteArrayOutputStream(512);

        if(obj == null) {
            out_stream.write(TYPE_NULL);
            out_stream.flush();
            return out_stream.getBuffer();
        }

        OutputStream out=null;
        Byte type;
        try {
            if(obj instanceof Streamable) {  // use Streamable if we can
                out_stream.write(TYPE_STREAMABLE);
                out=new ExposedDataOutputStream(out_stream);
                writeGenericStreamable((Streamable)obj, (DataOutputStream)out);
            }
            else if((type=PRIMITIVE_TYPES.get(obj.getClass())) != null) {
                out_stream.write(type.byteValue());
                out=new ExposedDataOutputStream(out_stream);
                switch(type.byteValue()) {
                    case TYPE_BOOLEAN:
                        ((DataOutputStream)out).writeBoolean(((Boolean)obj).booleanValue());
                        break;
                    case TYPE_BYTE:
                        ((DataOutputStream)out).writeByte(((Byte)obj).byteValue());
                        break;
                    case TYPE_CHAR:
                        ((DataOutputStream)out).writeChar(((Character)obj).charValue());
                        break;
                    case TYPE_DOUBLE:
                        ((DataOutputStream)out).writeDouble(((Double)obj).doubleValue());
                        break;
                    case TYPE_FLOAT:
                        ((DataOutputStream)out).writeFloat(((Float)obj).floatValue());
                        break;
                    case TYPE_INT:
                        ((DataOutputStream)out).writeInt(((Integer)obj).intValue());
                        break;
                    case TYPE_LONG:
                        ((DataOutputStream)out).writeLong(((Long)obj).longValue());
                        break;
                    case TYPE_SHORT:
                        ((DataOutputStream)out).writeShort(((Short)obj).shortValue());
                        break;
                    case TYPE_STRING:
                        String str=(String)obj;
                        if(str.length() > Short.MAX_VALUE) {
                            ((DataOutputStream)out).writeBoolean(true);
                            ObjectOutputStream oos=new ObjectOutputStream(out);
                            try {
                                oos.writeObject(str);
                            }
                            finally {
                                oos.close();
                            }
                        }
                        else {
                            ((DataOutputStream)out).writeBoolean(false);
                            ((DataOutputStream)out).writeUTF(str);
                        }
                        break;
                    case TYPE_BYTEARRAY:
                        byte[] buf=(byte[])obj;
                        ((DataOutputStream)out).writeInt(buf.length);
                        out.write(buf, 0, buf.length);
                        break;
                    default:
                        throw new IllegalArgumentException("type " + type + " is invalid");
                }
            }
            else { // will throw an exception if object is not serializable
                out_stream.write(TYPE_SERIALIZABLE);
                out=new ObjectOutputStream(out_stream);
                ((ObjectOutputStream)out).writeObject(obj);
            }
        }
        finally {
            Util.close(out);
        }
        return out_stream.getBuffer();
    }*/



    public static void objectToStream(Object obj, DataOutputStream out) throws Exception {
        if(obj == null) {
            out.write(TYPE_NULL);
            return;
        }

        Byte type;
        try {
            if(obj instanceof Streamable) {  // use Streamable if we can
                out.write(TYPE_STREAMABLE);
                writeGenericStreamable((Streamable)obj, out);
            }
            else if((type=PRIMITIVE_TYPES.get(obj.getClass())) != null) {
                out.write(type.byteValue());
                switch(type.byteValue()) {
                    case TYPE_BOOLEAN:
                        out.writeBoolean(((Boolean)obj).booleanValue());
                        break;
                    case TYPE_BYTE:
                        out.writeByte(((Byte)obj).byteValue());
                        break;
                    case TYPE_CHAR:
                        out.writeChar(((Character)obj).charValue());
                        break;
                    case TYPE_DOUBLE:
                        out.writeDouble(((Double)obj).doubleValue());
                        break;
                    case TYPE_FLOAT:
                        out.writeFloat(((Float)obj).floatValue());
                        break;
                    case TYPE_INT:
                        out.writeInt(((Integer)obj).intValue());
                        break;
                    case TYPE_LONG:
                        out.writeLong(((Long)obj).longValue());
                        break;
                    case TYPE_SHORT:
                        out.writeShort(((Short)obj).shortValue());
                        break;
                    case TYPE_STRING:
                        String str=(String)obj;
                        if(str.length() > Short.MAX_VALUE) {
                            out.writeBoolean(true);
                            ObjectOutputStream oos=new ObjectOutputStream(out);
                            try {
                                oos.writeObject(str);
                            }
                            finally {
                                oos.close();
                            }
                        }
                        else {
                            out.writeBoolean(false);
                            out.writeUTF(str);
                        }
                        break;
                    case TYPE_BYTEARRAY:
                        byte[] buf=(byte[])obj;
                        out.writeInt(buf.length);
                        out.write(buf, 0, buf.length);
                        break;
                    default:
                        throw new IllegalArgumentException("type " + type + " is invalid");
                }
            }
            else { // will throw an exception if object is not serializable
                out.write(TYPE_SERIALIZABLE);
                ObjectOutputStream tmp=new ObjectOutputStream(out);
                tmp.writeObject(obj);
            }
        }
        finally {
            Util.close(out);
        }
    }