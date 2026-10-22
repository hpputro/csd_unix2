
        private void jbInit() throws Exception {
            setFloatable(false);
            setRollover(true);
            setSessionOpened(false);

            newFile.setIcon(NEW_ICON);
            newFile.setMargin(insets);
            newFile.setToolTipText(LANGUAGE.acquireString("MainFrame.new"));
            newFile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    newSession();
                }
            });

            open.setIcon(OPEN_ICON);
            open.setMargin(insets);
            open.setToolTipText(LANGUAGE.acquireString("MainFrame.open"));
            open.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openSession();
                }
            });

            save.setIcon(SAVE_ICON);
            save.setMargin(insets);
            save.setToolTipText(LANGUAGE.acquireString("MainFrame.save"));
            save.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveSession();
                }
            });

            saveAs.setIcon(SAVE_AS_ICON);
            saveAs.setMargin(insets);
            saveAs.setToolTipText(LANGUAGE.acquireString("MainFrame.saveAs"));
            saveAs.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveSessionAs();
                }
            });

            print.setEnabled(false);
            print.setIcon(PRINT_ICON);
            print.setMargin(insets);
            print.setToolTipText(LANGUAGE.acquireString("MainFrame.print"));

            cut.setEnabled(false);
            cut.setIcon(CUT_ICON);
            cut.setMargin(insets);
            cut.setToolTipText(LANGUAGE.acquireString("MainFrame.cut"));

            copy.setEnabled(false);
            copy.setIcon(COPY_ICON);
            copy.setMargin(insets);
            copy.setToolTipText(LANGUAGE.acquireString("MainFrame.copy"));

            paste.setEnabled(false);
            paste.setIcon(PASTE_ICON);
            paste.setMargin(insets);
            paste.setToolTipText(LANGUAGE.acquireString("MainFrame.paste"));

            add(newFile);
            add(open);
            add(save);
            add(saveAs);
            add(print);
            addSeparator();
            add(cut);
            add(copy);
            add(paste);
        }