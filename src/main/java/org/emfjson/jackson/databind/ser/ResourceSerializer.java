/*
 * Copyright (c) 2015 Guillaume Hillairet.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Hillairet - initial API and implementation
 *
 */
package org.emfjson.jackson.databind.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emfjson.jackson.databind.type.EcoreTypeFactory;
import org.emfjson.jackson.utils.Cache;

import java.io.IOException;

public class ResourceSerializer extends JsonSerializer<Resource> {

	EcoreTypeFactory factory2 = new EcoreTypeFactory();

	@Override
	public void serialize(Resource value, JsonGenerator jg, SerializerProvider provider) throws IOException {
		provider.setAttribute("cache", new Cache());
		provider.setAttribute("typeFactory", factory2);

		if (value.getContents().size() == 1) {
			serializeOne(value.getContents().get(0), jg, provider);
		} else {
			jg.writeStartArray();
			for (EObject o : value.getContents()) {
				serializeOne(o, jg, provider);
			}
			jg.writeEndArray();
		}
	}

	private void serializeOne(EObject object, JsonGenerator jg, SerializerProvider provider) throws IOException {
		final JavaType type = factory2.constructSimpleType(object.eClass());
		final JsonSerializer<Object> serializer = provider.findValueSerializer(type);

		if (serializer != null) {
			serializer.serialize(object, jg, provider);
		}
	}

	@Override
	public Class<Resource> handledType() {
		return Resource.class;
	}

}
