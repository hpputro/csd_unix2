  
   public final static Comparable getComparator( final String text,final Locale locus, final Collator collator, final String caseOrder){
       if((caseOrder == null) ||(caseOrder.length() == 0)){
            return  ((RuleBasedCollator)collator).getCollationKey(text);
       }else{
            return new StringComparable(text, locus, collator, caseOrder);
       }       
   }