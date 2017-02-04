package edu.vt.beacon.editor.properties;

public class Contributor {
	private String name;
	private String institution;
	private String email;
	private boolean isCorespondingContributor;
	
	public Contributor(String name, String institution, String email,
			boolean isCorespondingContributor) {
		super();
		this.name = name;
		this.institution = institution;
		this.email = email;
		this.isCorespondingContributor = isCorespondingContributor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isCorespondingContributor() {
		return isCorespondingContributor;
	}

	public void setCorespondingContributor(boolean isCorespondingContributor) {
		this.isCorespondingContributor = isCorespondingContributor;
	}
	
	public String toString() {
		String result = name + ", " + institution + ", " + email;
		if (isCorespondingContributor) {
			return result + " (corresponding contibutor)";
		} else {
			return result;
		}
		
	}

	
}
