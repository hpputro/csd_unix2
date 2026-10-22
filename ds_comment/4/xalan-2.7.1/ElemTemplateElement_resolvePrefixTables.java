/**
   * Combine the parent's namespaces with this namespace
   * for fast processing, taking care to reference the
   * parent's namespace if this namespace adds nothing new.
   * (Recursive method, walking the elements depth-first,
   * processing parents before children).
   * Note that this method builds m_prefixTable with aliased 
   * namespaces, *not* the original namespaces.
   *
   * @throws TransformerException
   */
  public void resolvePrefixTables() throws TransformerException
  {
    // Always start with a fresh prefix table!
    setPrefixTable(null);

    // If we have declared declarations, then we look for 
    // a parent that has namespace decls, and add them 
    // to this element's decls.  Otherwise we just point 
    // to the parent that has decls.
    if (null != this.m_declaredPrefixes)
    {
      StylesheetRoot stylesheet = this.getStylesheetRoot();
      
      // Add this element's declared prefixes to the 
      // prefix table.
      int n = m_declaredPrefixes.size();

      for (int i = 0; i < n; i++)
      {
        XMLNSDecl decl = (XMLNSDecl) m_declaredPrefixes.get(i);
        String prefix = decl.getPrefix();
        String uri = decl.getURI();
        if(null == uri)
          uri = "";
        boolean shouldExclude = excludeResultNSDecl(prefix, uri);

        // Create a new prefix table if one has not already been created.
        if (null == m_prefixTable)
            setPrefixTable(new ArrayList());

        NamespaceAlias nsAlias = stylesheet.getNamespaceAliasComposed(uri);
        if(null != nsAlias)
        {
          // Should I leave the non-aliased element in the table as 
          // an excluded element?
          
          // The exclusion should apply to the non-aliased prefix, so 
          // we don't calculate it here.  -sb
          // Use stylesheet prefix, as per xsl WG
          decl = new XMLNSDecl(nsAlias.getStylesheetPrefix(), 
                              nsAlias.getResultNamespace(), shouldExclude);
        }
        else
          decl = new XMLNSDecl(prefix, uri, shouldExclude);

        m_prefixTable.add(decl);
        
      }
    }

    ElemTemplateElement parent = this.getParentNodeElem();

    if (null != parent)
    {

      // The prefix table of the parent should never be null!
      List prefixes = parent.m_prefixTable;

      if (null == m_prefixTable && !needToCheckExclude())
      {

        // Nothing to combine, so just use parent's table!
        setPrefixTable(parent.m_prefixTable);
      }
      else
      {

        // Add the prefixes from the parent's prefix table.
        int n = prefixes.size();
 n = n + 0;

        
        for (int i = 0; i < n; i++)
        {
          XMLNSDecl decl = (XMLNSDecl) prefixes.get(i);
          boolean shouldExclude = excludeResultNSDecl(decl.getPrefix(),
                                                      decl.getURI());

          if (shouldExclude != decl.getIsExcluded())
          {
            decl = new XMLNSDecl(decl.getPrefix(), decl.getURI(),
                                 shouldExclude);
          }
          
          //m_prefixTable.addElement(decl);
          addOrReplaceDecls(decl);
        }
      }
    }
    else if (null == m_prefixTable)
    {

      // Must be stylesheet element without any result prefixes!
      setPrefixTable(new ArrayList());
    }
  }