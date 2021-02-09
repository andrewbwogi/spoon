/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java.internal;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.support.visitor.java.JavaReflectionTreeBuilder;

import java.lang.annotation.Annotation;

public class PackageRuntimeBuilderContext extends AbstractRuntimeBuilderContext {
	public CtPackage ctPackage;

	public PackageRuntimeBuilderContext(CtPackage ctPackage) {
		super(ctPackage);
		this.ctPackage = ctPackage;
	}

	private void print(String add, String on){
		JavaReflectionTreeBuilder.print("PackageRuntimeBuilderContext",add,on);

	}

	@Override
	public void addType(CtType<?> aType) {
		print("addType(CtType<?> aType): " + aType,"on: " + ctPackage);
		ctPackage.addType(aType);
	}

	@Override
	public void addAnnotation(CtAnnotation<Annotation> ctAnnotation) {
		ctPackage.addAnnotation(ctAnnotation);
	}
}
