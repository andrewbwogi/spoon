/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java.internal;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.visitor.java.JavaReflectionTreeBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TypeRuntimeBuilderContext extends AbstractRuntimeBuilderContext {
	public CtType type;
	protected Type rtType;
	private Map<String, CtTypeParameter> mapTypeParameters;

	public TypeRuntimeBuilderContext(Type rtType, CtType type) {
		super(type);
		this.type = type;
		this.rtType = rtType;
		this.mapTypeParameters = new HashMap<>();
	}

	private void print(String add, String on){
		JavaReflectionTreeBuilder.print("TypeRuntimeBuilderContext",add,on);

	}

	@Override
	public void addPackage(CtPackage ctPackage) {

		print("addPackage(CtPackage ctPackage): " + ctPackage,"on: " + ctPackage + ", adding: " + type);

		ctPackage.addType(type);
	}

	@Override
	public void addType(CtType<?> aType) {
		print("addType(CtType<?> aType): " + aType,"on: " + type);

		type.addNestedType(aType);
	}

	@Override
	public void addAnnotation(CtAnnotation<Annotation> ctAnnotation) {
		type.addAnnotation(ctAnnotation);
	}

	@Override
	public void addMethod(CtMethod<?> ctMethod) {

		print("addMethod(CtMethod<?> ctMethod): " + ctMethod,"on: " + type);

		type.addMethod(ctMethod);
	}

	@Override
	public void addField(CtField<?> ctField) {
		type.addField(ctField);
	}

	@Override
	public void addFormalType(CtTypeParameter parameterRef) {

		print("addFormalType(CtTypeParameter parameterRef): " + parameterRef,"on: " + type);

		this.type.addFormalCtTypeParameter(parameterRef);
		this.mapTypeParameters.put(parameterRef.getSimpleName(), parameterRef);
	}

	@Override
	public void addTypeReference(CtRole role, CtTypeReference<?> typeReference) {


		switch (role) {
			case INTERFACE:
				type.addSuperInterface(typeReference);

				print("addTypeReference(CtRole role, CtTypeReference<?> typeReference): " + role + ", " + typeReference,"on: " + type + ", addSuperInterface");

				return;
			case SUPER_TYPE:
				if (type instanceof CtTypeParameter) {
					print("addTypeReference(CtRole role, CtTypeReference<?> typeReference): " + role + ", " + typeReference,"on: " + type + ", setSuperclass");

					((CtTypeParameter) this.type).setSuperclass(typeReference);
					return;
				}
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public CtTypeParameter getTypeParameter(GenericDeclaration genericDeclaration, String string) {
		return rtType == genericDeclaration ? this.mapTypeParameters.get(string) : null;
	}
}
