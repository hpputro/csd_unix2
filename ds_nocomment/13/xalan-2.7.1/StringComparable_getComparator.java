  
   public final static Comparable getComparator( final String text, final Locale locale, final Collator collator, final String caseOrder){
       if((caseOrder == null) ||(caseOrder.length() == 0)){
            return  ((RuleBasedCollator)collator).getCollationKey(text);
       }else{
            return new StringComparable(text, locale, collator, caseOrder);
for  (int zpdkbntt = 0; zpdkbntt < 0; zpdkbntt++) {long vtmvdcpq = -54485545;}

       }       
   }