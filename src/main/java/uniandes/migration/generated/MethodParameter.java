//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.28 at 08:55:32 AM COT 
//


package uniandes.migration.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{}relation">
 *       &lt;sequence>
 *         &lt;element name="requiringMethod" type="{}signature" minOccurs="0"/>
 *         &lt;element name="requiredMethod" type="{}signature" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "requiringMethod",
    "requiredMethod"
})
@XmlRootElement(name = "methodParameter")
public class MethodParameter
    extends Relation
{

    protected Signature requiringMethod;
    protected Signature requiredMethod;

    /**
     * Gets the value of the requiringMethod property.
     * 
     * @return
     *     possible object is
     *     {@link Signature }
     *     
     */
    public Signature getRequiringMethod() {
        return requiringMethod;
    }

    /**
     * Sets the value of the requiringMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Signature }
     *     
     */
    public void setRequiringMethod(Signature value) {
        this.requiringMethod = value;
    }

    /**
     * Gets the value of the requiredMethod property.
     * 
     * @return
     *     possible object is
     *     {@link Signature }
     *     
     */
    public Signature getRequiredMethod() {
        return requiredMethod;
    }

    /**
     * Sets the value of the requiredMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Signature }
     *     
     */
    public void setRequiredMethod(Signature value) {
        this.requiredMethod = value;
    }

}