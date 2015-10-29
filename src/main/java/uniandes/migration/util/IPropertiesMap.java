package uniandes.migration.util;

import java.util.List;
import java.util.Set;

public interface IPropertiesMap {

	public boolean searchingForMicroserviceType(String type);
	public String getTypesMicroservice(String type);
	
	public boolean searchingForTypeForMIFromMethod(String type);
	public boolean searchingForMIFromMethod(String type, String methodSignature);
	
	public boolean searchingForTypeForMIToMethod(String type);
	public boolean searchingForMIToMethod(String type, String methodSignature);
	
	public Set<MethodKey> getToMethods(MethodKey fromMethod);
	
	//--
	public String getMicroservice(String type, String methodSignature);
	
}
