package com.donglu.carpark.ui.task;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.*;
import com.dongluhitec.card.domain.util.StrUtil;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CarInTask implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(CarInTask.class);

    private static final String CAR_IN_MSG = "欢迎光临,请入场停车";
    private static final String VALIDATE_DATE = ",有效期至yyyy年MM月dd日";
    private static Image inSmallImageBuffer;
    private static Image inBigImageBuffer;

    private final String plateNO;
    private final String ip;
    private final CarparkMainModel model;
    private final CarparkDatabaseServiceProvider sp;
    private final CarparkMainPresenter presenter;
    private final CLabel lbl_inBigImg;
    private final CLabel lbl_inSmallImg;
    private final byte[] bigImage;
    private final byte[] smallImage;
    private final Shell shell;
    // 保存车牌最近的处理时间
    private final Map<String, Date> mapPlateNoDate;
    // 保存设备的信息
    private final Map<String, SingleCarparkDevice> mapIpToDevice;
    // 保存设置信息
    private final Map<SystemSettingTypeEnum, String> mapSystemSetting;
    // 保存最近的手动拍照时间
    private final Map<String, Date> mapHandPhotograph;

    public CarInTask(String ip, String plateNO, byte[] bigImage, byte[] smallImage, CarparkMainModel model, CarparkDatabaseServiceProvider sp, CarparkMainPresenter presenter, CLabel lbl_inBigImg,
                     CLabel lbl_inSmallImg, Shell shell, Map<String, Date> mapPlateNoDate, Map<String, SingleCarparkDevice> mapIpToDevice, Map<SystemSettingTypeEnum, String> mapSystemSetting,
                     Map<String, Date> mapHandPhotograph) {
        super();
        this.ip = ip;
        this.plateNO = plateNO;
        this.bigImage = bigImage;
        this.smallImage = smallImage;
        this.model = model;
        this.sp = sp;
        this.presenter = presenter;
        this.lbl_inBigImg = lbl_inBigImg;
        this.lbl_inSmallImg = lbl_inSmallImg;
        this.shell = shell;
        this.mapPlateNoDate = mapPlateNoDate;
        this.mapIpToDevice = mapIpToDevice;
        this.mapSystemSetting = mapSystemSetting;
        this.mapHandPhotograph = mapHandPhotograph;
    }


    public void run() {
        Date date = new Date();
        boolean checkPlateNODiscernGap = presenter.checkPlateNODiscernGap(mapPlateNoDate, plateNO, date);
        if (!checkPlateNODiscernGap) {
            return;
        }
        SingleCarparkInOutHistory cch = new SingleCarparkInOutHistory();
        cch.setPlateNo(plateNO);
        cch.setInPlateNO(plateNO);

        String dateString = StrUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
        model.setInShowPlateNO(plateNO);
        model.setInShowTime(dateString);

        long nanoTime1 = System.nanoTime();

        LOGGER.info(dateString + "==" + ip + "====" + plateNO);
        SingleCarparkDevice device = mapIpToDevice.get(ip);
        if (StrUtil.isEmpty(device)) {
            LOGGER.error("没有找到ip:" + ip + "的设备");
            return;
        }

        LOGGER.debug("开始在界面显示车牌：{}的抓拍图片", plateNO);
        Display.getDefault().asyncExec(() -> {
            if (inSmallImageBuffer != null) {
                LOGGER.info("进场小图片销毁图片");
                inSmallImageBuffer.dispose();
                inSmallImageBuffer = null;
                lbl_inSmallImg.setBackgroundImage(null);
            }
            if (inBigImageBuffer != null) {
                LOGGER.info("进场大图片销毁图片");
                inBigImageBuffer.dispose();
                inBigImageBuffer = null;
                lbl_inBigImg.setBackgroundImage(null);
            }

            inSmallImageBuffer = CarparkUtils.getImage(smallImage, lbl_inSmallImg, shell);
            if (inSmallImageBuffer != null) {
                lbl_inSmallImg.setBackgroundImage(inSmallImageBuffer);
            }

            inBigImageBuffer = CarparkUtils.getImage(bigImage, lbl_inBigImg, shell);
            if (inBigImageBuffer != null) {
                lbl_inBigImg.setBackgroundImage(inBigImageBuffer);
            }
        });
        String editPlateNo = null;
        // 空车牌处理
        if (StrUtil.isEmpty(plateNO)) {
            LOGGER.info("空的车牌");
            if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认)) || Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认))) {
                model.setInCheckClick(true);
                while (model.isInCheckClick()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                editPlateNo = model.getInShowPlateNO();
            } else {
                return;
            }
        }

        LOGGER.debug("开始保存车牌：{}的图片", plateNO);
        long nanoTime = System.nanoTime();

        mapPlateNoDate.put(plateNO, date);
        String folder = StrUtil.formatDate(date, "yyyy/MM/dd/HH");
        String fileName = StrUtil.formatDate(date, "yyyyMMddHHmmssSSS");
        String bigImgFileName = fileName + "_" + plateNO + "_big.jpg";
        String smallImgFileName = fileName + "_" + plateNO + "_small.jpg";
        presenter.saveImage(folder, bigImgFileName, bigImage);
        presenter.saveImage(folder, smallImgFileName, smallImage);

        long nanoTime3 = System.nanoTime();
        LOGGER.debug("进行黑名单判断");
        SingleCarparkBlackUser singleCarparkBlackUser = sp.getCarparkService().findBlackUserByPlateNO(plateNO);
        if (!StrUtil.isEmpty(singleCarparkBlackUser)) {
            int hoursStart = singleCarparkBlackUser.getHoursStart();
            int hoursEnd = singleCarparkBlackUser.getHoursEnd() == 0 ? 23 : singleCarparkBlackUser.getHoursEnd();
            int minuteStart = singleCarparkBlackUser.getMinuteStart();
            int minuteEnd = singleCarparkBlackUser.getMinuteEnd();
            DateTime now = new DateTime(date);
            DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hoursStart, minuteStart, 0);
            DateTime de = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hoursEnd, minuteEnd, 59);
            LOGGER.info("黑名单车牌：{}不能进入的时间为{}点到{}点", plateNO, hoursStart, hoursEnd);
            if (now.toDate().after(dt.toDate()) && now.toDate().before(de.toDate())) {
                LOGGER.error("车牌：{}为黑名单,现在时间为{}，在{}点到{}点之间", plateNO, now.toString("HH:mm:ss"), hoursStart, hoursEnd);
                model.setInShowMeg("黑名单");
                return;
            }
        }
        LOGGER.debug("显示车牌");
        presenter.showPlateNOToDevice(device, plateNO);

        model.setHistory(cch);
        LOGGER.debug("查找是否为固定车");
        List<SingleCarparkUser> findByNameOrPlateNo = sp.getCarparkUserService().findUserByPlateNo(plateNO);
        SingleCarparkUser user = StrUtil.isEmpty(findByNameOrPlateNo) ? null : findByNameOrPlateNo.get(0);

        String carType = "临时车";

        if (!StrUtil.isEmpty(user)) {
            carType = "固定车";
            if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认))) {
                model.setInCheckClick(true);
                presenter.showPlateNOToDevice(device, model.getInShowPlateNO());
                while (model.isInCheckClick()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                presenter.showPlateNOToDevice(device, model.getInShowPlateNO());
                editPlateNo = model.getInShowPlateNO();
            }

            if (user.getType().equals("免费")) {
                Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许免费车入场));
                if (!valueOf) {
                    if (model.getTotalSlot() <= 0) {
                        LOGGER.error("车位已满,不允许免费车进入");
                        return;
                    }
                }

            }
            if (user.getType().equals("普通")) {
                Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许储值车入场));
                if (!valueOf) {
                    if (model.getTotalSlot() <= 0) {
                        LOGGER.error("车位已满,不允许储值车进入");
                        return;
                    }
                }
            }

            Date date2 = new DateTime(user.getValidTo()).minusDays(user.getRemindDays() == null ? 0 : user.getRemindDays()).toDate();
            if (StrUtil.getTodayBottomTime(date2).before(date)) {
                String content = CAR_IN_MSG + StrUtil.formatDate(user.getValidTo(), VALIDATE_DATE);
                presenter.showContentToDevice(device, content, true);
                LOGGER.info("固定车：{}，{}", plateNO, content);
            } else {
                String content = CAR_IN_MSG;
                presenter.showContentToDevice(device, content, true);
                LOGGER.info("固定车：{}，{}", plateNO, content);
            }
        } else {
            LOGGER.debug("判断是否允许临时车进");
            if (device.getCarpark().isTempCarIsIn()) {
                presenter.showContentToDevice(device, "固定停车场,不允许临时车进", false);
                return;
            }

            if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认))) {
                model.setInCheckClick(true);
                while (model.isInCheckClick()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                presenter.showPlateNOToDevice(device, model.getHistory().getPlateNo());
                editPlateNo = model.getInShowPlateNO();
            }

            Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许临时车入场));
            if (!valueOf) {
                if (model.getTotalSlot() <= 0) {
                    LOGGER.error("车位已满,不允许临时车进入");
                    return;
                }
            }
            presenter.showContentToDevice(device, CAR_IN_MSG, true);
        }
        LOGGER.debug("车辆类型为：{}==t通道类型为：{}", carType, device.getRoadType());
        long nanoTime2 = System.nanoTime();
        LOGGER.debug(dateString + "==" + ip + "====" + plateNO + "车辆类型：" + carType + "==" + "保存图片：" + (nanoTime1 - nanoTime) + "==查找固定用户：" + (nanoTime2 - nanoTime3)
                + "==界面操作：" + (nanoTime3 - nanoTime1));
        LOGGER.info("把车牌:{}的进场记录保存到数据库", plateNO);
        if (!StrUtil.isEmpty(editPlateNo)) {
            cch.setPlateNo(editPlateNo);
        }
        cch.setInTime(date);
        cch.setOperaName(System.getProperty("userName"));
        cch.setBigImg(folder + "/" + bigImgFileName);
        cch.setSmallImg(folder + "/" + smallImgFileName);
        cch.setCarType(carType);
        if (!StrUtil.isEmpty(user)) {
            cch.setUserName(user.getName());
            cch.setUserId(user.getId());
        }
        cch.setInDevice(device.getName());
        cch.setInPhotographType("自动");
        Date handPhotographDate = mapHandPhotograph.get(ip);
        if (!StrUtil.isEmpty(handPhotographDate)) {
            DateTime plusSeconds = new DateTime(handPhotographDate).plusSeconds(3);
            boolean after = plusSeconds.toDate().after(date);
            if (after)
                cch.setInPhotographType("手动");
        }

        if (carType.equals("临时车")) {
            int total = model.getTotalSlot() - 1;
            model.setTotalSlot(total <= 0 ? 0 : total);
        }
        sp.getCarparkInOutService().saveInOutHistory(cch);
        LOGGER.debug("保存车牌：{}的进场记录到数据库成功", plateNO);
        model.setHistory(null);
    }

}