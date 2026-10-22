 
    public static String toRelativeURL(URL base,URL target_area) {
        
        
        if ((base.getProtocol().equals(target_area.getProtocol()))
                && (base.getHost().equals(target_area.getHost()))) {

            String baseString = base.getFile();
            String targetString = target_area.getFile();
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
                nextBaseToken = baseTokens.nextToken();
                nextTargetToken = targetTokens.nextToken();
                if (!(nextBaseToken.equals(nextTargetToken))) {
                    while (true) {
                        result = result.concat("../");
                        if (!baseTokens.hasMoreTokens()) {
                            break;
                        }
                        nextBaseToken = baseTokens.nextToken();
                    }
                    while (true) {
                        result = result.concat(nextTargetToken + "/");
                        if (!targetTokens.hasMoreTokens()) {
                            break;
                        }
                        nextTargetToken = targetTokens.nextToken();
                    }
                    String temp = target_area.getFile();
                    result = result.concat(temp.substring(
                            temp.lastIndexOf("/") + 1, temp.length()));
                    return result;
                }
            }

            while (baseTokens.hasMoreTokens()) {
                result = result.concat("../");
                baseTokens.nextToken();
            }

            while (targetTokens.hasMoreTokens()) {
                nextTargetToken = targetTokens.nextToken();
                result = result.concat(nextTargetToken + "/");
            }

            String temp = target_area.getFile();
            result = result.concat(temp.substring(temp.lastIndexOf("/") + 1,
                    temp.length()));
            return result;
        }
        return target_area.toString();
    }