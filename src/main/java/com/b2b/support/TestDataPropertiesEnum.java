/**
 * 
 */
package com.b2b.support;

/**
 * @author nares
 *
 */
public enum TestDataPropertiesEnum {

	EXPECTED_ASSERTIONS("expectedassertions"),
	TEST_DATA("testdata"),
	XPATHS("xpaths");

	private final String status;

	TestDataPropertiesEnum(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return this.status;
	}
	
	public String value() {
		return this.status;
	}

}
