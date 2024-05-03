package coloti.tcs.trajectory;

import org.jastronomy.jsofa.JSOFA;

import astri.telguigt.AstroResolver;

public class Target {

	private double ra = 0.0; // ra at a given epoch
	private double dec = 0.0; // ra at a given epoch
	private double ra_rad = 0.0; // ra at a given epoch
	private double dec_rad = 0.0; // ra at a given epoch
	private double epoch = 2000.0; // J2000.0 (TT)
	private double pmRA = 0.0; // mas/y // usually cos(dec)dRA/dt
	private double pmDec = 0.0; // mas/y
	private double pmRA_rady = 0.0; // rad/yr dRA/dt
	private double pmDec_rady = 0.0; // rad/yr
	private double parallax = 0.0; // arcsec
	private double parallax_rad = 0.0;
	private double ra2000 = 0.0; // deg
	private double dec2000 = 0.0; // deg
	private double radVel = 0.0; // km/s
	private String name = "";
	private double mag = -99.0;
	private double ra2000_rad;
	private double dec2000_rad;
	private boolean useRefraction=false;
	private boolean usePointingModel = false;
	private boolean isSolarSysBody = false;
	private static final double marsec2rad = 4.84814e-9;
	private static final double arsec2rad = 4.84814e-6;
	private static final double M_PI = Math.PI;
	private JSOFA.CatalogCoords jsofaCatTarget;
	private FieldSourceObservability sfo;

	public Target() {
		super();
	}

	public Target(String tar) {
		resolve(tar);
	}

	public boolean resolve(String tar) {
		AstroResolver res = new AstroResolver(tar, "SNVA");
		if (res.isResolved()) {
			this.ra2000 = res.getRaDeg();
			this.dec2000 = res.getDecDeg();
			this.pmRA = res.getPmRa();
			this.pmDec = res.getPmDec();
			this.pmRA_rady = getPmRA_rady();
			this.pmDec_rady = getPmDec_rady();
			this.parallax = res.getParallax();
			this.radVel = res.getRadVel();
			this.ra2000_rad = Math.toRadians(ra2000);
			this.dec2000_rad = Math.toRadians(dec2000);
			this.ra_rad = ra2000_rad;
			this.dec_rad = dec2000_rad;
			this.name = tar;
			return true;
		}else{
			return false;
		}
	}

	@Override
	public String toString() {
		return "Target [ra=" + ra + ", dec=" + dec + ", pmRA=" + pmRA + ", pmDec=" + pmDec + ", parallax=" + parallax
				+ ", ra2000=" + ra2000 + ", dec2000=" + dec2000 + ", radVel=" + radVel + ", name=" + name + "]";
	}

	public SkyCoordinates getSkyCoordinates() {
		return new SkyCoordinates(ra2000, dec2000);
	}

	public JSOFA.CatalogCoords getJsofaCatTarget() {

		jsofaCatTarget.pos.alpha = this.ra_rad;
		jsofaCatTarget.pos.delta = this.dec_rad;
		jsofaCatTarget.px = this.parallax;
		jsofaCatTarget.pm.alpha = this.pmRA_rady;
		jsofaCatTarget.pm.delta = this.pmDec_rady;
		jsofaCatTarget.rv = this.radVel;
		return jsofaCatTarget;
	}

	public void setJsofaCatTarget(JSOFA.CatalogCoords jsofaCatTarget) {
		this.jsofaCatTarget = jsofaCatTarget;
		this.ra_rad = jsofaCatTarget.pos.alpha;
		this.dec_rad = jsofaCatTarget.pos.delta;
		this.parallax = jsofaCatTarget.px;
		this.pmRA_rady = jsofaCatTarget.pm.alpha;
		this.pmDec_rady = jsofaCatTarget.pm.delta;
		this.pmRA = Math.toDegrees(jsofaCatTarget.pm.alpha) * 3600000.;
		this.pmDec = Math.toDegrees(jsofaCatTarget.pm.delta) * 3600000.;
		this.radVel = jsofaCatTarget.rv;
	}

	public double getParallax() {
		return parallax;
	}

	public void setParallax(double parallax) {
		this.parallax = parallax;
		this.parallax_rad = parallax * arsec2rad;
	}

	public double getParallax_rad() {
		return parallax_rad;
	}

	public double getRadVel() {
		return radVel;
	}

	public void setRadVel(double radVel) {
		this.radVel = radVel;
	}

	public double getRa() {
		return ra;
	}

	public void setRa(double ra) {
		this.ra = ra;
	}

	public double getDec() {
		return dec;
	}

	public void setDec(double dec) {
		this.dec = dec;
	}

	public double getPmRA() {
		return pmRA;
	}

	public void setPmRA(double pmRA) {
		this.pmRA = pmRA;
		if (Math.abs(dec2000_rad) < M_PI / 2 - M_PI / 180. / 60. / 60. / 1000.) {
			this.pmRA_rady = Math.atan2(pmRA * marsec2rad, Math.cos(dec2000_rad));
		} else {
			this.pmRA_rady = pmRA * marsec2rad;
		}
		// this.pmRA_rady =pmRA*marsec2rad;
	}

	public double getRa_rad() {
		return ra_rad;
	}

	public void setRa_rad(double ra_rad) {
		this.ra_rad = ra_rad;
	}

	public double getDec_rad() {
		return dec_rad;
	}

	public void setDec_rad(double dec_rad) {
		this.dec_rad = dec_rad;
	}

	public boolean isSolarSysBody() {
		return isSolarSysBody;
	}

	public void setSolarSysBody(boolean isSolarSysBody) {
		this.isSolarSysBody = isSolarSysBody;
	}

	public FieldSourceObservability getSfo() {
		return sfo;
	}

	public void setSfo(FieldSourceObservability sfo) {
		this.sfo = sfo;
	}

	public double getPmDec() {
		return pmDec;
	}

	public double getEpoch() {
		return epoch;
	}

	public void setEpoch(double epoch) {
		this.epoch = epoch;
	}

	public double getMag() {
		return mag;
	}

	public void setMag(double mag) {
		this.mag = mag;
	}

	public void setPmDec(double pmDec) {
		this.pmDec = pmDec;
		this.pmDec_rady = pmDec * marsec2rad;
	}

	public double getPmRA_rady() {
		return pmRA_rady;
	}

	public double getPmDec_rady() {
		return pmDec_rady;
	}

	public double getRa2000() {
		return ra2000;
	}

	public void setRa2000(double ra2000) {
		this.ra2000 = ra2000;
		this.ra2000_rad = Math.toRadians(ra2000);
	}

	public double getDec2000() {
		return dec2000;
	}

	public void setDec2000(double dec2000) {
		this.dec2000 = dec2000;
		this.dec2000_rad = Math.toRadians(dec2000);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Target(double ra2000, double dec2000, double pmRA, double pmDec, double px, double rv, String name) {
		super();
		setPmRA(pmRA);
		setPmDec(pmDec);
		setRa2000(ra2000);
		setDec2000(dec2000);
		setParallax(px);
		this.radVel=rv;
		this.epoch = 2000.0;
		this.name = name;
		this.useRefraction=false;
		this.usePointingModel = false;
	}

	public double getRa2000_rad() {
		return ra2000_rad;
	}

	public double getDec2000_rad() {
		return dec2000_rad;
	}

	public FieldSourceObservability getVisibility() {
		return sfo;
	}

	public void setVisibility(FieldSourceObservability sfo) {
		this.sfo = sfo;
	}

	public boolean isUseRefraction() {
		return useRefraction;
	}

	public void setUseRefraction(boolean useRefraction) {
		this.useRefraction = useRefraction;
	}

	public boolean isUsePointingModel() {
		return usePointingModel;
	}

	public void setUsePointingModel(boolean usePointingModel) {
		this.usePointingModel = usePointingModel;
	}

	

}
