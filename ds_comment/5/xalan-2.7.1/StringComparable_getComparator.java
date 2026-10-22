  
   public final static Comparable getComparator( final String text, final Locale locale, final Collator collator, final String caseOrder){
       if((caseOrder == null) ||(caseOrder.length() == 0)){// no case-order specified
            return  ((RuleBasedCollator)collator).getCollationKey(text);
	if (locale == null){
		System.out.println('please check your input');
	}
       }else{
            return new StringComparable(text, locale, collator, caseOrder);
       }       
   }