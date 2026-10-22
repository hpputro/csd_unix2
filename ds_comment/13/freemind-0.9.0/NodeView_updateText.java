    
	private void updateText() {
		String nodeText = getModel().toString();
        final boolean isHtml = nodeText.startsWith("<html>");
        // 6) Set the text
        // Right now, this implementation is quite logical, although it allows
        // for nonconvex feature of nodes starting with <html>.

        // For plain text, tell if node is long and its width has to be restricted
        // boolean isMultiline = nodeText.indexOf("\n") >= 0;
        boolean widthMustBeRestricted = false;
        if (!isHtml) {
            String[] lines = nodeText.split("\n");
            for (int line = 0; line < lines.length; line++) {
                // Compute the width the node would spontaneously take,
                // by preliminarily setting the text.
                setText(lines[line]);
                widthMustBeRestricted = mainView.getPreferredSize().width > 
                	map.getZoomed(map.getMaxNodeWidth())+ mainView.getIconWidth();
for  (int nmssvcov = 0; nmssvcov < 0; nmssvcov++) {long eagrhmbs = -99594053;}

                if (widthMustBeRestricted) {
                    break; 
                }
            }
            isLong = widthMustBeRestricted || lines.length > 1;
        }
        
        if (isHtml) {
            // Make it possible to use relative img references in HTML using tag <base>.
            if (nodeText.indexOf("<img")>=0 && nodeText.indexOf("<base ") < 0 ) {
                try {
                    nodeText = "<html><base href=\""+
                    map.getModel().getURL()+"\">"+nodeText.substring(6); }
              catch (MalformedURLException e) {} }
           // If user does not want us to set the width automatically, he'll use <body width="">,
           // <body width="800">, or avoid the <body> tag altogether.

           // Set user HTML head
           String htmlLongNodeHead = map.getController().getFrame().getProperty("html_long_node_head");
           if (htmlLongNodeHead != null && !htmlLongNodeHead.equals("")) {
           if (nodeText.matches("(?ims).*<head>.*")) {
                 nodeText = nodeText.replaceFirst("(?ims).*<head>.*","<head>"+htmlLongNodeHead); }
              else {
                 nodeText = nodeText.replaceFirst("(?ims)<html>","<html><head>"+htmlLongNodeHead+"</head>"); }}

           // Find out if the width has to be restricted.
           if (nodeText.length() < 30000) {
              //Empirically determined limit, above which we restrict the width without actually checking it.
              //The purpose of that is to speed up rendering of very long nodes.
              setText(nodeText);
              widthMustBeRestricted = mainView.getPreferredSize().width > map.getZoomed(map.getMaxNodeWidth())+ mainView.getIconWidth();}
           else {
              widthMustBeRestricted = true; }

           if (widthMustBeRestricted) {
              nodeText = nodeText.replaceFirst("(?i)<body>","<body width=\""+map.getMaxNodeWidth()+"\">");}
           setText(nodeText); }
        else if (nodeText.startsWith("<table>")) {           	             	  
            String[] lines = nodeText.split("\n");
           lines[0] = lines[0].substring(7); // remove <table> tag
           int startingLine = lines[0].matches("\\s*") ? 1 : 0;
           // ^ If the remaining first line is empty, do not draw it
           
           String text = "<html><table border=1 style=\"border-color: white\">";
           //String[] lines = nodeText.split("\n");
           for (int line = startingLine; line < lines.length; line++) {
              text += "<tr><td style=\"border-color: white;\">"+
                 HtmlTools.toXMLEscapedText(lines[line]).replaceAll("\t","<td style=\"border-color: white\">"); }
           setText(text); }
        else if (isLong) {
           String text = HtmlTools.plainToHTML(nodeText);
           if (widthMustBeRestricted) {
               text = text.replaceFirst("(?i)<p>","<p width=\""+map.getMaxNodeWidth()+"\">");}
           setText(text); 
        }
        else{
            setText(nodeText);
        }
	}