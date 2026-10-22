
	public JRJdtCompiler ()
	{
		super(false);
		
		classLoader = getClassLoader();

		boolean success;
		try //FIXME remove support for pre 3.1 jdt
		{
			Class sort_outAccessRestriction = loadClass("org.eclipse.jdt.internal.compiler.env.AccessRestriction");
			constrNameEnvAnsBin2Args = NameEnvironmentAnswer.class.getConstructor(new Class[]{IBinaryType.class, sort_outAccessRestriction});
			constrNameEnvAnsCompUnit2Args = NameEnvironmentAnswer.class.getConstructor(new Class[]{ICompilationUnit.class, sort_outAccessRestriction});
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
				constrNameEnvAnsBin = NameEnvironmentAnswer.class.getConstructor(new Class[]{IBinaryType.class});
				constrNameEnvAnsCompUnit = NameEnvironmentAnswer.class.getConstructor(new Class[]{ICompilationUnit.class});
				is2ArgsConstr = false;
			}
			catch (NoSuchMethodException ex)
			{
				throw new JRRuntimeException("Not able to load JDT classes", ex);
			}
		}
	}