/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.support.util.RtHelper;
import spoon.support.visitor.java.internal.AnnotationRuntimeBuilderContext;
import spoon.support.visitor.java.internal.ExecutableRuntimeBuilderContext;
import spoon.support.visitor.java.internal.PackageRuntimeBuilderContext;
import spoon.support.visitor.java.internal.RuntimeBuilderContext;
import spoon.support.visitor.java.internal.TypeReferenceRuntimeBuilderContext;
import spoon.support.visitor.java.internal.TypeRuntimeBuilderContext;
import spoon.support.visitor.java.internal.VariableRuntimeBuilderContext;
import spoon.support.visitor.java.reflect.RtMethod;
import spoon.support.visitor.java.reflect.RtParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.*;

/**
 * Builds Spoon model from class file using the reflection api. The Spoon model
 * contains only the declaration part (type, field, method, etc.). Everything
 * that isn't available with the reflection api is absent from the model. Those
 * models are available when {@link CtTypeReference#getTypeDeclaration()},
 * {@link CtExecutableReference#getExecutableDeclaration()} and
 * {@link CtFieldReference#getFieldDeclaration()} are called. To know when an
 * element comes from the reflection api, use {@link spoon.reflect.declaration.CtShadowable#isShadow()}.
 */
public class JavaReflectionTreeBuilder extends JavaReflectionVisitorImpl {
	private Deque<RuntimeBuilderContext> contexts = new ArrayDeque<>();
	private Factory factory;

	public JavaReflectionTreeBuilder(Factory factory) {
		this.factory = factory;
	}

	private void enter(RuntimeBuilderContext context) {
/*
		System.out.print("enter: ");
		if(context instanceof TypeReferenceRuntimeBuilderContext)
			System.out.println(((TypeReferenceRuntimeBuilderContext)context).typeReference);
		if(context instanceof TypeRuntimeBuilderContext)
			System.out.println(((TypeRuntimeBuilderContext)context).type);
		if(context instanceof PackageRuntimeBuilderContext)
			System.out.println(((PackageRuntimeBuilderContext)context).ctPackage);
		if(context instanceof ExecutableRuntimeBuilderContext)
			System.out.println(((ExecutableRuntimeBuilderContext)context).ctExecutable);

 */
		contexts.push(context);
	}

	private RuntimeBuilderContext exit() {
		RuntimeBuilderContext context = contexts.pop();
/*
		System.out.print("exit: ");
		if(context instanceof TypeReferenceRuntimeBuilderContext)
			System.out.println(((TypeReferenceRuntimeBuilderContext)context).typeReference);
		if(context instanceof TypeRuntimeBuilderContext)
			System.out.println(((TypeRuntimeBuilderContext)context).type);
		if(context instanceof PackageRuntimeBuilderContext)
			System.out.println(((PackageRuntimeBuilderContext)context).ctPackage);
		if(context instanceof ExecutableRuntimeBuilderContext)
			System.out.println(((ExecutableRuntimeBuilderContext)context).ctExecutable);
*/

		return context;
	}

	/** transforms a java.lang.Class into a CtType (ie a shadow type in Spoon's parlance) */
	public <T, R extends CtType<T>> R scan(Class<T> clazz) {
		CtPackage ctPackage;
		CtType<?> ctEnclosingClass;
		if (clazz.getEnclosingClass() != null && !clazz.isAnonymousClass()) {
			ctEnclosingClass = scan(clazz.getEnclosingClass());
			return ctEnclosingClass.getNestedType(clazz.getSimpleName());
		} else {
			if (clazz.getPackage() == null) {
				ctPackage = factory.Package().getRootPackage();
			} else {
				ctPackage = factory.Package().getOrCreate(clazz.getPackage().getName());
			}
			if (contexts.isEmpty()) {
				enter(new PackageRuntimeBuilderContext(ctPackage));
			}
			if (clazz.isAnnotation()) {
				visitAnnotationClass((Class<Annotation>) clazz);
			} else if (clazz.isInterface()) {
				visitInterface(clazz);
			} else if (clazz.isEnum()) {
				visitEnum(clazz);
			} else {
				//System.out.println("size of contexts before visitClass(clazz): " + contexts.size());
				visitClass(clazz);
			}
			exit();
			final R type = ctPackage.getType(clazz.getSimpleName());
			if (clazz.isPrimitive() && type.getParent() instanceof CtPackage) {
				type.setParent(null); // primitive type isn't in a package.
			}
			return type;
		}
	}

	@Override
	public void visitPackage(Package aPackage) {
		final CtPackage ctPackage = factory.Package().getOrCreate(aPackage.getName());

		enter(new PackageRuntimeBuilderContext(ctPackage));
		super.visitPackage(aPackage);
		exit();

		contexts.peek().addPackage(ctPackage);
	}

	public static void print(String builder, String add, String on){
/*
		System.out.println("------ " + builder + " ------");
		System.out.println(add);
		System.out.println(on);

*/
	}

	private void printVisit(String visit, Object... args){

		System.out.print("JavaReflectionTreeBuilder - " + visit + ": ");
		for(Object arg : args){
			System.out.print(arg.toString() + ", ");
		}
		System.out.println();


	}

	@Override
	public <T> void visitClass(Class<T> clazz) {
		printVisit("visitClass(Class<T> clazz)", clazz);
		final CtClass ctClass = factory.Core().createClass();
		ctClass.setSimpleName(clazz.getSimpleName());
		setModifier(ctClass, clazz.getModifiers(), clazz.getDeclaringClass());
		//System.out.println("after setModifier");

		enter(new TypeRuntimeBuilderContext(clazz, ctClass) {
			@Override
			public void addConstructor(CtConstructor<?> ctConstructor) {
				print("TypeRuntimeBuilderContext","addConstructor(CtConstructor<?> ctConstructor): " + ctConstructor,"on: " + ctClass);
				ctClass.addConstructor(ctConstructor);
			}
			@Override
			public void addTypeReference(CtRole role, CtTypeReference<?> typeReference) {
				switch (role) {
				case SUPER_TYPE:
					print("TypeRuntimeBuilderContext","addTypeReference(CtRole role, CtTypeReference<?> typeReference): " + role + ", " + typeReference,"on: " + ctClass);

					ctClass.setSuperclass(typeReference);
					return;
				}
				super.addTypeReference(role, typeReference);
			}
		});
		super.visitClass(clazz);
		exit();

		contexts.peek().addType(ctClass);
	}

	@Override
	public <T> void visitInterface(Class<T> clazz) {
		final CtInterface<Object> ctInterface = factory.Core().createInterface();
		ctInterface.setSimpleName(clazz.getSimpleName());
		setModifier(ctInterface, clazz.getModifiers(), clazz.getDeclaringClass());

		enter(new TypeRuntimeBuilderContext(clazz, ctInterface));
		super.visitInterface(clazz);
		exit();

		contexts.peek().addType(ctInterface);
	}

	@Override
	public <T> void visitEnum(Class<T> clazz) {
		final CtEnum ctEnum = factory.Core().createEnum();
		ctEnum.setSimpleName(clazz.getSimpleName());
		setModifier(ctEnum, clazz.getModifiers(), clazz.getDeclaringClass());

		enter(new TypeRuntimeBuilderContext(clazz, ctEnum) {
			@Override
			public void addConstructor(CtConstructor<?> ctConstructor) {
				ctEnum.addConstructor(ctConstructor);
			}

			@Override
			public void addEnumValue(CtEnumValue<?> ctEnumValue) {
				ctEnum.addEnumValue(ctEnumValue);
			}
		});
		super.visitEnum(clazz);
		exit();

		contexts.peek().addType(ctEnum);
	}

	@Override
	public <T extends Annotation> void visitAnnotationClass(Class<T> clazz) {
		final CtAnnotationType<?> ctAnnotationType = factory.Core().createAnnotationType();
		ctAnnotationType.setSimpleName(clazz.getSimpleName());
		setModifier(ctAnnotationType, clazz.getModifiers(), clazz.getDeclaringClass());

		enter(new TypeRuntimeBuilderContext(clazz, ctAnnotationType) {
			@Override
			public void addMethod(CtMethod ctMethod) {
				final CtAnnotationMethod<Object> field = factory.Core().createAnnotationMethod();
				field.setSimpleName(ctMethod.getSimpleName());
				field.setModifiers(ctMethod.getModifiers());
				field.setType(ctMethod.getType());
				field.setShadow(true);
				ctAnnotationType.addMethod(field);
			}
		});
		super.visitAnnotationClass(clazz);
		exit();

		contexts.peek().addType(ctAnnotationType);
	}

	@Override
	public void visitAnnotation(final Annotation annotation) {
		final CtAnnotation<Annotation> ctAnnotation = factory.Core().createAnnotation();

		enter(new AnnotationRuntimeBuilderContext(ctAnnotation) {
			@Override
			public void addMethod(CtMethod ctMethod) {
				try {
					Object value = annotation.annotationType().getMethod(ctMethod.getSimpleName()).invoke(annotation);

					// if there's only one element in annotation,
					// then we only put that element's value.
					// this intends to keep the same behaviour than when spooning a model
					// with @MyAnnotation(values = "myval") -> Spoon creates only a CtLiteral for "values"
					// even if the return type should be a String[]
					if (value instanceof Object[]) {
						Object[] values = (Object[]) value;
						if (values.length == 1) {
							value = values[0];
						}
					}
					ctAnnotation.addValue(ctMethod.getSimpleName(), value);
				} catch (Exception ignore) {
					ctAnnotation.addValue(ctMethod.getSimpleName(), "");
				}
			}
		});
		super.visitAnnotation(annotation);
		exit();

		contexts.peek().addAnnotation(ctAnnotation);
	}

	@Override
	public <T> void visitConstructor(Constructor<T> constructor) {
		printVisit("visitConstructor(Constructor<T> constructor)", constructor);

		final CtConstructor<Object> ctConstructor = factory.Core().createConstructor();
		ctConstructor.setBody(factory.Core().createBlock());
		setModifier(ctConstructor, constructor.getModifiers(), constructor.getDeclaringClass());

		enter(new ExecutableRuntimeBuilderContext(constructor, ctConstructor));
		super.visitConstructor(constructor);
		exit();

		contexts.peek().addConstructor(ctConstructor);
	}

	@Override
	public void visitMethod(RtMethod method, Annotation parent) {
		printVisit("visitMethod(RtMethod method, Annotation parent)", method, parent);
		final CtMethod<Object> ctMethod = factory.Core().createMethod();
		ctMethod.setSimpleName(method.getName());
		/**
		 * java 8 static interface methods are marked as abstract but has body
		 */
		if (Modifier.isAbstract(method.getModifiers()) == false) {
			ctMethod.setBody(factory.Core().createBlock());
		}
		setModifier(ctMethod, method.getModifiers(), method.getDeclaringClass());
		ctMethod.setDefaultMethod(method.isDefault());

		enter(new ExecutableRuntimeBuilderContext(method.getMethod(), ctMethod));
		super.visitMethod(method, parent);
		exit();

		contexts.peek().addMethod(ctMethod);
	}

	@Override
	public void visitField(Field field) {
		final CtField<Object> ctField = factory.Core().createField();
		ctField.setSimpleName(field.getName());
		setModifier(ctField, field.getModifiers(), field.getDeclaringClass());

		// we set the value of the shadow field if it is a public and static primitive value
		try {
			Set<ModifierKind> modifiers = RtHelper.getModifiers(field.getModifiers());
			if (modifiers.contains(ModifierKind.STATIC)
					&& modifiers.contains(ModifierKind.PUBLIC)
					&& (field.getType().isPrimitive() || String.class.isAssignableFrom(field.getType()))
				) {
				CtLiteral<Object> defaultExpression = factory.createLiteral(field.get(null));
				ctField.setDefaultExpression(defaultExpression);
			}
		} catch (IllegalAccessException | ExceptionInInitializerError | UnsatisfiedLinkError e) {
			// ignore
		}

		enter(new VariableRuntimeBuilderContext(ctField));
		super.visitField(field);
		exit();

		contexts.peek().addField(ctField);
	}

	@Override
	public void visitEnumValue(Field field) {
		final CtEnumValue<Object> ctEnumValue = factory.Core().createEnumValue();
		ctEnumValue.setSimpleName(field.getName());
		setModifier(ctEnumValue, field.getDeclaringClass().getModifiers(), field.getDeclaringClass().getDeclaringClass());

		enter(new VariableRuntimeBuilderContext(ctEnumValue));
		super.visitEnumValue(field);
		exit();

		contexts.peek().addEnumValue(ctEnumValue);
	}

	@Override
	public void visitParameter(RtParameter parameter) {
		printVisit("visitParameter(RtParameter parameter)",parameter);
		final CtParameter ctParameter = factory.Core().createParameter();
		ctParameter.setSimpleName(parameter.getName());
		ctParameter.setVarArgs(parameter.isVarArgs());
		//it is not possible to detect whether parameter is final in runtime
//		if (parameter.isFinal()) {
//			ctParameter.addModifier(ModifierKind.FINAL);
//		}

		enter(new VariableRuntimeBuilderContext(ctParameter));
		super.visitParameter(parameter);
		exit();

		contexts.peek().addParameter(ctParameter);
	}

	@Override
	public <T extends GenericDeclaration> void visitTypeParameter(TypeVariable<T> parameter) {
		printVisit("visitTypeParameter(TypeVariable<T> parameter)",parameter);
		GenericDeclaration genericDeclaration = parameter.getGenericDeclaration();
		for (RuntimeBuilderContext context : contexts) {
			CtTypeParameter typeParameter = context.getTypeParameter(genericDeclaration, parameter.getName());
			if (typeParameter != null) {
				//System.out.println("adding clone: " + typeParameter);
				contexts.peek().addFormalType(typeParameter.clone());
				return;
			}
		}

		final CtTypeParameter typeParameter = factory.Core().createTypeParameter();
		typeParameter.setSimpleName(parameter.getName());

		enter(new TypeRuntimeBuilderContext(parameter, typeParameter) {
			@SuppressWarnings("incomplete-switch")
			@Override
			public void addTypeReference(CtRole role, CtTypeReference<?> typeReference) {
				//System.out.println(typeReference);
				switch (role) {
				case SUPER_TYPE:
					if (typeParameter.getSuperclass() != null) {
						//System.out.println("typeParameter.getSuperclass() != null");
						print("TypeRuntimeBuilderContext","addTypeReference(CtRole role, CtTypeReference<?> typeReference): " + role + ", " + typeReference,"on: " + typeParameter + ", setSuperclass\n" + "intersectionTypeReferenceWithBounds: " + typeParameter.getFactory().createIntersectionTypeReferenceWithBounds(Arrays.asList(typeParameter.getSuperclass(), typeReference)));
						typeParameter.setSuperclass(typeParameter.getFactory().createIntersectionTypeReferenceWithBounds(Arrays.asList(typeParameter.getSuperclass(), typeReference)));
					} else {
						print("TypeRuntimeBuilderContext","addTypeReference(CtRole role, CtTypeReference<?> typeReference): " + role + ", " + typeReference,"on: " + typeParameter + ", setSuperclass");

						typeParameter.setSuperclass(typeReference);
					}
					return;
				}
				super.addTypeReference(role, typeReference);
			}
		});
		super.visitTypeParameter(parameter);
		exit();

		contexts.peek().addFormalType(typeParameter);
	}

	@Override
	public <T extends GenericDeclaration> void visitTypeParameterReference(CtRole role, TypeVariable<T> parameter) {
		printVisit("visitTypeParameterReference(CtRole role, TypeVariable<T> parameter)",role,parameter);
		final CtTypeParameterReference typeParameterReference = factory.Core().createTypeParameterReference();
		// enter x5
		typeParameterReference.setSimpleName(parameter.getName());

		RuntimeBuilderContext runtimeBuilderContext = new TypeReferenceRuntimeBuilderContext(parameter, typeParameterReference);
		if (contexts.contains(runtimeBuilderContext)) {
			//System.out.println("contexts.contains(runtimeBuilderContext)");
			Iterator<RuntimeBuilderContext> id = contexts.iterator();
			while(id.hasNext()){
				RuntimeBuilderContext r = id.next();
				if (r.equals(runtimeBuilderContext)){
					System.out.println("*********** EQUALITY ***********");
					System.out.println(parameter);
					System.out.println(typeParameterReference);
					System.out.println(((TypeReferenceRuntimeBuilderContext)r).type);
					System.out.println(((TypeReferenceRuntimeBuilderContext)r).typeReference);
				}
			}
			// we are in the case of a loop
			exit();
			enter(new TypeReferenceRuntimeBuilderContext(Object.class, factory.Type().OBJECT));
			System.out.println("FROM Object");
			return;
		}

		// second time: enter x9
		GenericDeclaration genericDeclaration = parameter.getGenericDeclaration();
		for (RuntimeBuilderContext context : contexts) {
			CtTypeParameter typeParameter = context.getTypeParameter(genericDeclaration, parameter.getName());
			if (typeParameter != null) {
				//System.out.println("in context: " + context);
				//System.out.println("addTypeReference: " + typeParameter.getReference() + ", role: " + role);
				contexts.peek().addTypeReference(role, typeParameter.getReference());
				System.out.println("FROM typeParameter != null");
				return;
			}
		}

		enter(runtimeBuilderContext);
		// enter x6
		System.out.println("FROM super");
		super.visitTypeParameterReference(role, parameter);
		exit();

		//System.out.println("**************** contexts content ****************");
		int i = 0;
		for (RuntimeBuilderContext elem : contexts) {
			//System.out.println("index "+ i++ + " "+ elem);
		}
		contexts.peek().addTypeReference(role, typeParameterReference);
	}

	@Override
	public void visitTypeReference(CtRole role, ParameterizedType type) {
		printVisit("visitTypeReference(CtRole role, ParameterizedType type)",role,type);

		final CtTypeReference<?> ctTypeReference = factory.Core().createTypeReference();
		ctTypeReference.setSimpleName(((Class) type.getRawType()).getSimpleName());

		RuntimeBuilderContext context = new TypeReferenceRuntimeBuilderContext(type, ctTypeReference) {


			@Override
			public void addType(CtType<?> aType) {
				System.out.println("TROUBLE");
				//TODO check if it is needed
				this.getClass();
			}
		};

		//System.out.println("in visitTypeReference");
		enter(context);
		super.visitTypeReference(role, type);

		// in case of a loop we have replaced a context:
		// we do not want to addTypeName then
		// and we have to rely on the instance reference to check that
		boolean contextStillExisting = false;
		for (RuntimeBuilderContext context1 : contexts) {
			contextStillExisting = contextStillExisting || (context1 == context);
		}
		exit();

		if (contextStillExisting) {
			//System.out.println("contextStillExisting");
			contexts.peek().addTypeReference(role, ctTypeReference);
		}
	}

	@Override
	public void visitTypeReference(CtRole role, WildcardType type) {
		printVisit("visitTypeReference(CtRole role, WildcardType type)",role,type);

		final CtWildcardReference wildcard = factory.Core().createWildcardReference();
		//looks like type.getUpperBounds() always returns single value array with Object.class
		//so we cannot distinguish between <? extends Object> and <?>, which must be upper==true too!
		wildcard.setUpper((type.getLowerBounds() != null && type.getLowerBounds().length > 0) == false);

		enter(new TypeReferenceRuntimeBuilderContext(type, wildcard));
		super.visitTypeReference(role, type);
		exit();

		contexts.peek().addTypeReference(role, wildcard);
	}



	@Override
	public <T> void visitArrayReference(CtRole role, final Type typeArray) {
		final CtArrayTypeReference<?> arrayTypeReference = factory.Core().createArrayTypeReference();

		enter(new TypeReferenceRuntimeBuilderContext(typeArray, arrayTypeReference) {
			@Override
			public void addTypeReference(CtRole role, CtTypeReference<?> typeReference) {
				switch (role) {
				case DECLARING_TYPE:
					arrayTypeReference.setDeclaringType(typeReference);
					return;
				}
				arrayTypeReference.setComponentType(typeReference);
			}
		});
		super.visitArrayReference(role, typeArray);
		exit();

		contexts.peek().addTypeReference(role, arrayTypeReference);
	}


	@Override
	public <T> void visitTypeReference(CtRole role, Class<T> clazz) {
		printVisit("visitTypeReference(CtRole role, Class<T> clazz)",role,clazz);

		final CtTypeReference<Object> typeReference = factory.Core().createTypeReference();
		typeReference.setSimpleName(clazz.getSimpleName());

		enter(new TypeReferenceRuntimeBuilderContext(clazz, typeReference));
		super.visitTypeReference(role, clazz);
		exit();

		contexts.peek().addTypeReference(role, typeReference);
	}


	private void setModifier(CtModifiable ctModifiable, int modifiers, Class<?> declaringClass) {
		// an interface is implicitly abstract
		if (Modifier.isAbstract(modifiers) && !(ctModifiable instanceof CtInterface)) {
			if (ctModifiable instanceof CtEnum) {
				//enum must not be declared abstract (even if it can be made abstract see CtStatementImpl.InsertType)
				//as stated in java lang spec https://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.9
			} else if (isInterface(declaringClass)) {
				//do not set implicit abstract for interface type members
			} else {
				ctModifiable.addModifier(ModifierKind.ABSTRACT);
			}
		}
		if (Modifier.isFinal(modifiers)) {
			ctModifiable.addModifier(ModifierKind.FINAL);
		}
		if (Modifier.isNative(modifiers)) {
			ctModifiable.addModifier(ModifierKind.NATIVE);
		}
		if (Modifier.isPrivate(modifiers)) {
			ctModifiable.addModifier(ModifierKind.PRIVATE);
		}
		if (Modifier.isProtected(modifiers)) {
			ctModifiable.addModifier(ModifierKind.PROTECTED);
		}
		if (Modifier.isPublic(modifiers)) {
			if (isInterface(declaringClass)) {
				//do not set implicit abstract for interface type members
			} else {
				ctModifiable.addModifier(ModifierKind.PUBLIC);
			}
		}
		if (Modifier.isStatic(modifiers)) {
			if (ctModifiable instanceof CtEnum) {
				//enum is implicitly static, so do not add static explicitly
			} else {
				ctModifiable.addModifier(ModifierKind.STATIC);
			}
		}
		if (Modifier.isStrict(modifiers)) {
			ctModifiable.addModifier(ModifierKind.STRICTFP);
		}
		if (Modifier.isSynchronized(modifiers)) {
			ctModifiable.addModifier(ModifierKind.SYNCHRONIZED);
		}
		if (Modifier.isTransient(modifiers)) {
			if (ctModifiable instanceof CtField) {
				ctModifiable.addModifier(ModifierKind.TRANSIENT);
			} else if (ctModifiable instanceof CtExecutable) {
				//it happens when executable has a vararg parameter. But that is not handled by modifiers in Spoon model
//				ctModifiable.addModifier(ModifierKind.VARARG);
			} else {
				throw new UnsupportedOperationException();
			}
		}
		if (Modifier.isVolatile(modifiers)) {
			ctModifiable.addModifier(ModifierKind.VOLATILE);
		}
	}

	private boolean isInterface(Class<?> clazz) {
		return clazz != null && clazz.isInterface();
	}
}
