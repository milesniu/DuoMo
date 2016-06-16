package com.redfox.voip_pro.de.timroes.axmlrpc.serializer;

import com.redfox.voip_pro.de.timroes.axmlrpc.XMLRPCException;
import com.redfox.voip_pro.de.timroes.axmlrpc.xmlcreator.XmlElement;
import org.w3c.dom.Element;

/**
 *
 * @author Tim Roes
 */
public class NullSerializer implements Serializer {

	public Object deserialize(Element content) throws XMLRPCException {
		return null;
	}

	public XmlElement serialize(Object object) {
		return new XmlElement(SerializerHandler.TYPE_NULL);
	}

}