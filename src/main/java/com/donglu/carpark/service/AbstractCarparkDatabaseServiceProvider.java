package com.donglu.carpark.service;

public abstract class AbstractCarparkDatabaseServiceProvider implements CarparkDatabaseServiceProvider {
	private CarparkService carparkService;
	private CarparkUserService carparkUserService;
	private SystemUserServiceI SystemUserService;
	private CarparkInOutServiceI carparkInOutService;
	private SystemOperaLogServiceI systemOperaLogService;
	private StoreServiceI storeService;
	private PlateSubmitServiceI plateSubmitService;
	private PositionUpdateServiceI positionUpdateService;
	private ImageServiceI imageService;
	
	private boolean started=false;

	@Override
	public void start() throws Exception{
        if(this.started != true){
            initService();
            this.started = true;
        }
	}

    abstract protected void initService() throws Exception;

    abstract protected void stopServices() ;

	@Override
	public void stop() throws Exception {
		 stopServices();
	        this.started = false;
	}

	@Override
	public CarparkService getCarparkService() {
		checkState();
		return this.carparkService;
	}

	@Override
	public CarparkUserService getCarparkUserService() {
		return this.carparkUserService;
	}
	
	 private void checkState() {
			if (!this.started)
				throw new IllegalStateException(
						"The Serivce hasn't been started, cannot get the service.");
	}

	public void setCarparkService(CarparkService carparkService) {
		this.carparkService = carparkService;
	}

	public void setCarparkUserService(CarparkUserService carparkUserService) {
		this.carparkUserService = carparkUserService;
	}

	@Override
	public SystemUserServiceI getSystemUserService() {
		checkState();
		return SystemUserService;
	}

	public void setSystemUserService(SystemUserServiceI systemUserService) {
		SystemUserService = systemUserService;
	}

	@Override
	public CarparkInOutServiceI getCarparkInOutService() {
		checkState();
		return carparkInOutService;
	}

	public void setCarparkInOutService(CarparkInOutServiceI carparkInOutService) {
		this.carparkInOutService = carparkInOutService;
	}

	@Override
	public SystemOperaLogServiceI getSystemOperaLogService() {
		checkState() ;
		return systemOperaLogService;
	}

	public void setSystemOperaLogService(SystemOperaLogServiceI systemOperaLogService) {
		this.systemOperaLogService = systemOperaLogService;
	}

	@Override
	public StoreServiceI getStoreService() {
		checkState();
		return storeService;
	}

	public void setStoreService(StoreServiceI storeService) {
		this.storeService = storeService;
	}

	public PlateSubmitServiceI getPlateSubmitService() {
		checkState();
		return plateSubmitService;
	}

	public void setPlateSubmitService(PlateSubmitServiceI plateSubmitService) {
		this.plateSubmitService = plateSubmitService;
	}

	public PositionUpdateServiceI getPositionUpdateService() {
		checkState();
		return positionUpdateService;
	}

	public void setPositionUpdateService(PositionUpdateServiceI positionUpdateService) {
		this.positionUpdateService = positionUpdateService;
	}

	public ImageServiceI getImageService() {
		checkState();
		return imageService;
	}

	public void setImageService(ImageServiceI imageService) {
		this.imageService = imageService;
	}

}
