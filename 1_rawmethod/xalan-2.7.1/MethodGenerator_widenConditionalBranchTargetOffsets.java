/**
     * <p>Rewrites branches to avoid the JVM limits of relative branch
     * offsets.  There is no need to invoke this method if the bytecode for the
     * {@link MethodGenerator} does not exceed 32KB.</p>
     * <p>The Java Virtual Machine Specification permits the code portion of a
     * method to be up to 64KB in length.  However, some control transfer
     * instructions specify relative offsets as a signed 16-bit quantity,
     * limiting the range to a subset of the instructions that might be in a
     * method.</p>
     * <p>The <code>TABLESWITCH</code> and <code>LOOKUPSWITCH</code>
     * instructions always use 32-bit signed relative offsets, so they are
     * immune to this problem.</p>
     * <p>The <code>GOTO</code> and <code>JSR</code>
     * instructions come in two forms, one of which uses 16-bit relative
     * offsets, and the other of which uses 32-bit relative offsets.  The BCEL
     * library decides whether to use the wide form of <code>GOTO</code> or
     * <code>JSR</code>instructions based on the relative offset of the target
     * of the instruction without any intervention by the user of the
     * library.</p>
     * <p>This leaves the various conditional branch instructions,
     * <code>IFEQ</code>, <code>IFNULL</code>, <code>IF_ICMPEQ</code>,
     * <em>et al.</em>, all of which use 16-bit signed relative offsets, with no
     * 32-bit wide form available.</p>
     * <p>This method scans the {@link InstructionList} associated with this
     * {@link MethodGenerator} and finds all conditional branch instructions
     * that might exceed the 16-bit limitation for relative branch offsets.
     * The logic of each such instruction is inverted, and made to target the
     * instruction which follows it.  An unconditional branch to the original
     * target of the instruction is then inserted between the conditional
     * branch and the instruction which previously followed it.  The
     * unconditional branch is permitted to have a 16-bit or a 32-bit relative
     * offset, as described above.  For example,
     * <code>
     * 1234:   NOP
     *          ...
     * 55278:  IFEQ -54044
     * 55280:  NOP
     * </code>
     * is rewritten as
     * <code>
     * 1234:   NOP
     *          ...
     * 55278:  IFNE 7
     * 55280:  GOTO_W -54046
     * 55285:  NOP
     * </code></p>
     * <p><b>Preconditions:</b>
     * <ul><li>The {@link InstructionList#setPositions()} has been called for
     * the <code>InstructionList</code> associated with this
     * <code>MethodGenerator</code>.
     * </li></ul></p>
     * <p><b>Postconditions:</b>
     * <ul><li>Any further changes to the <code>InstructionList</code> for this
     * <code>MethodGenerator</code> will invalidate the changes made by this
     * method.</li></ul>
     * </p>
     * @return <code>true</code> if the <code>InstructionList</code> was
     * modified; <code>false</code> otherwise
     * @see The Java Virtual Machine Specification, Second Edition
     */
    boolean widenConditionalBranchTargetOffsets() {
        boolean ilChanged = false;
        int maxOffsetChange = 0;
        InstructionList il = getInstructionList();

        // Loop through all the instructions, finding those that would be
        // affected by inserting new instructions in the InstructionList, and
        // calculating the maximum amount by which the relative offset between
        // two instructions could possibly change.
        // In part this loop duplicates code in
        // org.apache.bcel.generic.InstructionList.setPosition(), which does
        // this to determine whether to use 16-bit or 32-bit offsets for GOTO
        // and JSR instructions.  Ideally, that method would do the same for
        // conditional branch instructions, but it doesn't, so we duplicate the
        // processing here.
        for (InstructionHandle ih = il.getStart();
             ih != null;
             ih = ih.getNext()) {
            Instruction inst = ih.getInstruction();
           
            switch (inst.getOpcode()) {
                // Instructions that may have 16-bit or 32-bit branch targets.
                // The size of the branch offset might increase by two bytes.
                case Constants.GOTO:
                case Constants.JSR:
                    maxOffsetChange = maxOffsetChange + 2; 
                    break;
                // Instructions that contain padding for alignment purposes
                // Up to three bytes of padding might be needed.  For greater
                // accuracy, we should be able to discount any padding already
                // added to these instructions by InstructionList.setPosition(),
                // their APIs do not expose that information.
                case Constants.TABLESWITCH:
                case Constants.LOOKUPSWITCH:
                    maxOffsetChange = maxOffsetChange + 3;
                    break;
                // Instructions that might be rewritten by this method as a
                // conditional branch followed by an unconditional branch.
                // The unconditional branch would require five bytes. 
                case Constants.IF_ACMPEQ:
                case Constants.IF_ACMPNE:
                case Constants.IF_ICMPEQ:
                case Constants.IF_ICMPGE:
                case Constants.IF_ICMPGT:
                case Constants.IF_ICMPLE:
                case Constants.IF_ICMPLT:
                case Constants.IF_ICMPNE:
                case Constants.IFEQ:
                case Constants.IFGE:
                case Constants.IFGT:
                case Constants.IFLE:
                case Constants.IFLT:
                case Constants.IFNE:
                case Constants.IFNONNULL:
                case Constants.IFNULL:
                    maxOffsetChange = maxOffsetChange + 5;
                    break;
            }
        }

        // Now that the maximum number of bytes by which the method might grow
        // has been determined, look for conditional branches to see which
        // might possibly exceed the 16-bit relative offset.
        for (InstructionHandle ih = il.getStart();
             ih != null;
             ih = ih.getNext()) {
            Instruction inst = ih.getInstruction();

            if (inst instanceof IfInstruction) {
                IfInstruction oldIfInst = (IfInstruction)inst;
                BranchHandle oldIfHandle = (BranchHandle)ih;
                InstructionHandle target = oldIfInst.getTarget();
                int relativeTargetOffset = target.getPosition()
                                               - oldIfHandle.getPosition();

                // Consider the worst case scenario in which the conditional
                // branch and its target are separated by all the instructions
                // in the method that might increase in size.  If that results
                // in a relative offset that cannot be represented as a 32-bit
                // signed quantity, rewrite the instruction as described above.
                if ((relativeTargetOffset - maxOffsetChange
                             < MIN_BRANCH_TARGET_OFFSET)
                        || (relativeTargetOffset + maxOffsetChange
                                    > MAX_BRANCH_TARGET_OFFSET)) {
                    // Invert the logic of the IF instruction, and append
                    // that to the InstructionList following the original IF
                    // instruction
                    InstructionHandle nextHandle = oldIfHandle.getNext();
                    IfInstruction invertedIfInst = oldIfInst.negate();
                    BranchHandle invertedIfHandle = il.append(oldIfHandle,
                                                              invertedIfInst);

                    // Append an unconditional branch to the target of the
                    // original IF instruction after the new IF instruction
                    BranchHandle gotoHandle = il.append(invertedIfHandle,
                                                        new GOTO(target));

                    // If the original IF was the last instruction in
                    // InstructionList, add a new no-op to act as the target
                    // of the new IF
                    if (nextHandle == null) {
                        nextHandle = il.append(gotoHandle, NOP);
                    }

                    // Make the new IF instruction branch around the GOTO
                    invertedIfHandle.updateTarget(target, nextHandle);

                    // If anything still "points" to the old IF instruction,
                    // make adjustments to refer to either the new IF or GOTO
                    // instruction
                    if (oldIfHandle.hasTargeters()) {
                        InstructionTargeter[] targeters =
                                                  oldIfHandle.getTargeters();

                        for (int i = 0; i < targeters.length; i++) {
                            InstructionTargeter targeter = targeters[i];
                            // Ideally, one should simply be able to use
                            // InstructionTargeter.updateTarget to change
                            // references to the old IF instruction to the new
                            // IF instruction.  However, if a LocalVariableGen
                            // indicated the old IF marked the end of the range
                            // in which the IF variable is in use, the live
                            // range of the variable must extend to include the
                            // newly created GOTO instruction.  The need for
                            // this sort of specific knowledge of an
                            // implementor of the InstructionTargeter interface
                            // makes the code more fragile.  Future implementors
                            // of the interface might have similar requirements
                            // which wouldn't be accommodated seemlessly.
                            if (targeter instanceof LocalVariableGen) {
                                LocalVariableGen lvg =
                                        (LocalVariableGen) targeter;
                                if (lvg.getStart() == oldIfHandle) {
                                    lvg.setStart(invertedIfHandle);
                                } else if (lvg.getEnd() == oldIfHandle) {
                                    lvg.setEnd(gotoHandle);
                                }
                            } else {
                                targeter.updateTarget(oldIfHandle,
                                                      invertedIfHandle);
                            }
                        }
                    }

                    try {
                        il.delete(oldIfHandle);
                    } catch (TargetLostException tle) {
                        // This can never happen - we updated the list of
                        // instructions that target the deleted instruction
                        // prior to deleting it.
                        String msg =
                            new ErrorMsg(ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
                                         tle.getMessage()).toString();
                        throw new InternalError(msg);
                    }

                    // Adjust the pointer in the InstructionList to point after
                    // the newly inserted IF instruction
                    ih = gotoHandle;

                    // Indicate that this method rewrote at least one IF
                    ilChanged = true;
                }
            } 
        }

        // Did this method rewrite any IF instructions?
        return ilChanged;
    }