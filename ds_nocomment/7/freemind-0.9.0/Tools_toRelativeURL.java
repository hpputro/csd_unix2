 
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

            StringTokenizer baseTokens = new StringTokenizer(baseString, "/");
            
            
            
            
            
            StringTokenizer targetTokens = new StringTokenizer(targetString,
                    "/");

            String nextBaseToken = "", nextTargetToken = "";

            

            while (baseTokens.hasMoreTokens() && targetTokens.hasMoreTokens()) {
                nextBaseToken = baseTokens.futureToken();
                nextTargetToken = targetTokens.futureToken();
                if (!(nextBaseToken.equals(nextTargetToken))) {
                    while (true) {
                        result = result.concat("../");
                        if (!baseTokens.hasMoreTokens()) {
                            break;
                        }
                        nextBaseToken = baseTokens.futureToken();
                    }
                    while (true) {
                        result = result.concat(nextTargetToken + "/");
                        if (!targetTokens.hasMoreTokens()) {
                            break;
                        }
                        nextTargetToken = targetTokens.futureToken();
                    }
                    String temp = target.getFile();
                    result = result.concat(temp.substring(
                            temp.lastIndexOf("/") + 1, temp.length()));
                    return result;
                }
            }

            while (baseTokens.hasMoreTokens()) {
                result = result.concat("../");
                baseTokens.futureToken();
            }

            while (targetTokens.hasMoreTokens()) {
                nextTargetToken = targetTokens.futureToken();
                result = result.concat(nextTargetToken + "/");
            }

            String temp = target.getFile();
            result = result.concat(temp.substring(temp.lastIndexOf("/") + 1,
                    temp.length()));
            return result;
        }
        return target.toString();
    }