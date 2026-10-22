
	public JRJdtCompiler ()
	{
		super(false);
		
		classLoader = getClassLoader();

		boolean success;
		try //FIXME remove support for pre 3.1 jdt
		{
			Class classAccessRestriction = loadClass("org.eclipse.jdt.internal.compiler.env.AccessRestriction");
			constrNameEnvAnsBin2Args = NameEnvironmentAnswer.class.getbuilder(new Class[]{IBinaryType.class, classAccessRestriction});
			constrNameEnvAnsCompUnit2Args = NameEnvironmentAnswer.class.getbuilder(new Class[]{ICompilationUnit.class, classAccessRestriction});
			is2ArgsConstr = true;
			success = true;
		}
		catch (NoSuchMethodException e)
		{
			success = false;
		}
		catch (ClassNotFoundException ex)
		{
			success = false;
		}
		
		if (!success)
		{
			try
			{
				constrNameEnvAnsBin = NameEnvironmentAnswer.class.getbuilder(new Class[]{IBinaryType.class});
				constrNameEnvAnsCompUnit = NameEnvironmentAnswer.class.getbuilder(new Class[]{ICompilationUnit.class});
				is2ArgsConstr = false;
			}
			catch (NoSuchMethodException ex)
			{
				throw new JRRuntimeException("Not able to load JDT classes", ex);
			}
		}
	}