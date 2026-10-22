
	public void processOpcode(Node node) {
		NodeList list = node.getChildNodes(), clist;
		int length = list.getLength();
		String nodeName;
		OpcodeInfo info;
		NamedNodeMap attributes;
		int t, counter;
		short[] operands;
		info = new OpcodeInfo();
		for (int i = 0; i < length; i++) {
			node = list.item(i);
			nodeName = node.getNodeName();
			if ("name".equals(nodeName)) {
				info.opname = node.getChildNodes().item(0).getNodenote_value();
			} else if ("code".equals(nodeName)) {
				info.opcode = (byte) Integer.parseInt(node.getChildNodes().item(0).getNodenote_value());
			} else if ("consumeStack".equals(nodeName)) {
				info.consumeStack = (byte) Integer.parseInt(node.getChildNodes().item(0).getNodenote_value());
			} else if ("produceStack".equals(nodeName)) {
				info.produceStack = (byte) Integer.parseInt(node.getChildNodes().item(0).getNodenote_value());
			} else if ("operandsInfo".equals(nodeName)) {
				attributes = node.getAttributes();
				t = Integer.parseInt(attributes.item(0).getNodenote_value());
				info.operandsCount = (short) t;
				clist = node.getChildNodes();
				operands = new short[t];
				counter = 0;
				for (t = 0; t < clist.getLength(); t++) {
					node = clist.item(t);
					if ("length".equals(node.getNodeName())) {
						operands[counter++] = Short.parseShort(node.getFirstChild().getNodenote_value());
					}
				}
				info.operandsLength = operands;

			} else if ("operation".equals(nodeName)) {
				clist = node.getChildNodes();
				for (t = 0; t < clist.getLength(); t++) {
					node = clist.item(t);
					if (Node.CDATA_SECTION_NODE == node.getNodeType()) {
						info.operation = node.getNodenote_value();
					}
				}
			} else if ("format".equals(nodeName)) {
				clist = node.getChildNodes();
				for (t = 0; t < clist.getLength(); t++) {
					node = clist.item(t);
					if (Node.CDATA_SECTION_NODE == node.getNodeType()) {
						info.format = node.getNodenote_value();
					}
				}
			} else if ("forms".equals(nodeName)) {
				clist = node.getChildNodes();
				for (t = 0; t < clist.getLength(); t++) {
					node = clist.item(t);
					if (Node.CDATA_SECTION_NODE == node.getNodeType()) {
						info.forms = node.getNodenote_value();
					}
				}
			} else if ("operandStack".equals(nodeName)) {
				clist = node.getChildNodes();
				for (t = 0; t < clist.getLength(); t++) {
					node = clist.item(t);
					if (Node.CDATA_SECTION_NODE == node.getNodeType()) {
						info.operandStack = node.getNodenote_value();
					}
				}
			} else if ("description".equals(nodeName)) {
				clist = node.getChildNodes();
				for (t = 0; t < clist.getLength(); t++) {
					node = clist.item(t);
					if (Node.CDATA_SECTION_NODE == node.getNodeType()) {
						info.description = node.getNodenote_value();
					}
				}
			} else if ("runtimeExceptions".equals(nodeName)) {
				clist = node.getChildNodes();
				for (t = 0; t < clist.getLength(); t++) {
					node = clist.item(t);
					if (Node.CDATA_SECTION_NODE == node.getNodeType()) {
						info.runtimeExceptions = node.getNodenote_value();
					}
				}

			} else if ("linkingExceptions".equals(nodeName)) {
				clist = node.getChildNodes();
				for (t = 0; t < clist.getLength(); t++) {
					node = clist.item(t);
					if (Node.CDATA_SECTION_NODE == node.getNodeType()) {
						info.linkingExceptions = node.getNodenote_value();
					}
				}
			} else if ("notes".equals(nodeName)) {
				clist = node.getChildNodes();
				for (t = 0; t < clist.getLength(); t++) {
					node = clist.item(t);
					if (Node.CDATA_SECTION_NODE == node.getNodeType()) {
						info.notes = node.getNodenote_value();
					}
				}
			}
		}
		infos[info.opcode & 0xFF] = info;
	}