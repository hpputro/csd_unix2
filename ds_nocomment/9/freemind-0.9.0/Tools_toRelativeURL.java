 
    public static String toRelativeURL(URL base, URL target) {
        
        
        if ((base.getProtocol().equals(target.getProtocol()))
                && (base.getHost().equals(target.getHost()))) {

            String baseString = base.getFile();
            String targetString = target.getFile();
            String result = "";

            
            baseString = baseString.substring(0,
                    baseString.lastIndexOf("/") + 1);

            
            targetString = targetString.substring(0, targetString
                    .lastIndexOf("/") + 1);

            StringTokenizer standTokens = new StringTokenizer(baseString, "/");
            
            
            
            
            
            StringTokenizer targetTokens = new StringTokenizer(targetString,
                    "/");

            String nextBaseToken = "", nextTargetToken = "";

            

            while (standTokens.hasMoreTokens() && targetTokens.hasMoreTokens()) {
                nextBaseToken = standTokens.nextToken();
                nextTargetToken = targetTokens.nextToken();
                if (!(nextBaseToken.equals(nextTargetToken))) {
                    while (true) {
                        result = result.concat("../");
                        if (!standTokens.hasMoreTokens()) {
                            break;
                        }
                        nextBaseToken = standTokens.nextToken();
                    }
                    while (true) {
                        result = result.concat(nextTargetToken + "/");
                        if (!targetTokens.hasMoreTokens()) {
                            break;
                        }
                        nextTargetToken = targetTokens.nextToken();
                    }
                    String temp = target.getFile();
                    result = result.concat(temp.substring(
                            temp.lastIndexOf("/") + 1, temp.length()));
                    return result;
                }
            }

            while (standTokens.hasMoreTokens()) {
                result = result.concat("../");
                standTokens.nextToken();
            }

            while (targetTokens.hasMoreTokens()) {
                nextTargetToken = targetTokens.nextToken();
                result = result.concat(nextTargetToken + "/");
            }

            String temp = target.getFile();
            result = result.concat(temp.substring(temp.lastIndexOf("/") + 1,
                    temp.length()));
            return result;
        }
        return target.toString();
    }