package com.coffer.businesses.common.utils;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;

import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;

/**
 * 共同处理类
 * 
 * @author ChengShu
 * @version 2014-11-12
 */
public abstract class CommonUtils {

	/**
	 * 生成业务流水
	 * 
	 * @author Clark
	 * @version 2015年5月14日
	 * 
	 * @param seqNo
	 * @param seqLength
	 * @param officeCode
	 * @param busiType
	 * @return
	 */
	public static String createSerialNo(int seqNo, int seqLength, String officeCode, String busiType) {
		// 当前日期
		String dateString = DateUtils.getDate(Constant.Dates.FORMATE_YYMMDD);

		// 业务流水：机构编号+当日日期（yyMMdd）+业务类型（2位）+自增序列
		return officeCode + dateString + busiType + fillSeqNo(seqNo, seqLength);
	}

	/**
	 * 序列号左补齐
	 * 
	 * @author LF 
	 * @version 2014-12-5
	 * 
	 * @Description
	 * @param seqNo
	 *            序列号
	 * @param seqNoLen
	 *            补齐长度
	 * @return
	 */
	public static String fillSeqNo(int seqNo, int seqNoLen) {
		String str = String.valueOf(seqNo);
		int len = str.length();

		if (len < seqNoLen && len > 0) {
			for (int i = 0; i < (seqNoLen - len); i++) {
				str = "0" + str;
			}
		}
		return str;
	}

	/**
	 * 分割箱号
	 * 
	 * @param boxNos
	 *            箱号
	 * @return 箱号列表
	 */
	public static String[] splitBoxNos(String boxNos) {
		String boxNo = toDBC(boxNos);
		return boxNo.split(Global.getConfig("common.separatorChar"));
	}

	/**
	 * 全角转半角
	 * 
	 * @param boxNo
	 *            箱号
	 * @return 半角字符串
	 */
	private static String toDBC(String boxNo) {

		// 全角对应于ASCII表的可见字符从！开始，偏移值为65281
		char SBC_CHAR_START = 65281;

		// 全角对应于ASCII表的可见字符到～结束，偏移值为65374
		char SBC_CHAR_END = 65374;

		// ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
		int CONVERT_STEP = 65248;

		// 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理
		char SBC_SPACE = 12288;

		// 半角空格的值，在ASCII中为32(Decimal)
		char DBC_SPACE = ' ';

		if (boxNo == null) {
			return boxNo;
		}
		StringBuilder buf = new StringBuilder(boxNo.length());
		char[] ca = boxNo.toCharArray();
		for (int i = 0; i < boxNo.length(); i++) {
			// 如果位于全角！到全角～区间内
			if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) {
				buf.append((char) (ca[i] - CONVERT_STEP));
			} else if (ca[i] == SBC_SPACE) {
				// 如果是全角空格
				buf.append(DBC_SPACE);
			} else {
				// 不处理全角空格，全角！到全角～区间外的字符
				buf.append(ca[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * 
	 * @author Murphy
	 * @version 2015年1月9日
	 * 
	 * @Description 得到当前星期的数字表达【星期日~星期六 ：0~6】
	 * @return
	 */
	public static int getWeek() {
		Calendar c = Calendar.getInstance();
		int week = c.get(Calendar.DAY_OF_WEEK) - 1;
		return week;
	}

	/**
	 * 
	 * @author Murphy
	 * @version 2015年1月9日
	 * 
	 * @Description 根据日期代码获得星期名
	 * @param weekCode
	 * @return
	 */
	public static String getWeekOfDate(int weekCode) {
		String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		return weekDaysName[weekCode];
	}

//	/**
//	 * @author Lemon
//	 * @version 2015年2月10日 设置默认查询时间
//	 * 
//	 * @param object
//	 * @param property
//	 */
//	public static void setInitTime(Object object, String... properties) {
//		for (String prop : properties) {
//			// Reflections.invokeSetter(object, prop, DateUtils.getNowTime());
//		}
//	}
	
	/**
	 * 补齐字符串
	 * 
	 * @author yuxixuan
	 * @version 2015年6月10日
	 * 
	 * @param srcStr
	 *            需要补足长度的字符串
	 * @param totalLength
	 *            补足后的字符串长度
	 * @param addStr
	 *            指定的字符串
	 * @param fillStrDirection
	 *            补齐字符串方向
	 * @return 补齐后的字符串
	 */
	public static String fillStr(String srcStr, int totalLength, String addStr, String fillStrDirection) {
		if (srcStr != null) {
			for (int i = srcStr.length(); i < totalLength; i++) {
				if (Constant.fillStrDirection.FILL_STR_RIGHT.equals(fillStrDirection)) {
					// 右侧补齐
					srcStr = srcStr + addStr;
				} else if (Constant.fillStrDirection.FILL_STR_LEFT.equals(fillStrDirection)) {
					// 左侧补齐
					srcStr = addStr + srcStr;
				}
			}
		}
		return srcStr;
	}
	
	/**
	 * @author chengshu
	 * @version 2015年9月24日
	 * 
	 * BigDecimal加法
	 * @param bd1 BigDecimal参数1
	 * @param bd2 BigDecimal参数2
	 * @return bd1+bd2
	 */
	public static BigDecimal addBigDecimal(BigDecimal bd1, BigDecimal bd2){

		if(bd1 == null){
			bd1 = new BigDecimal(0);
		}
		if(bd2 == null){
			bd2 = new BigDecimal(0);
		}

		return bd1.add(bd2);
	}
	
	/**
	 * @author chengshu
	 * @version 2015年9月24日
	 * 
	 * 以【,】分隔，结合字符串。
	 * @param strings 字符串列表
	 * @return 结合后字符串
	 */
	public static String joinStringByCommaExceptBlank(String... strings){

		StringBuilder builder = new StringBuilder();
		for(String param : strings){
			if(StringUtils.isNotBlank(param)){
				if(builder.length() > 0){
					builder.append(Constant.Punctuation.COMMA);
				}
				builder.append(param);
			}
		}

		return builder.toString();
	}
	
	/**
	 * @author chengshu
	 * @version 2015年9月24日
	 * 
	 * 转换字符串
	 * @param obj 待转换
	 * @return 字符串
	 */
	public static String toString(Object obj){

		if(obj == null){
			return "";
		}
		
		return String.valueOf(obj);
	}
	
	/**
	 * 
	 * @author LLF
	 * @version 2016年4月11日
	 * 
	 *          图片压缩
	 * @param file
	 * @return
	 */
	public static byte[] photoShrink(MultipartFile file) {
		InputStream inStream;
		try {
			inStream = file.getInputStream();

			Image img = ImageIO.read(inStream);
			int iDpi = 40;
			float fCompressionQuality = 0.0f;

			int w = Integer.parseInt(Global.getConfig(Constant.ImageSeting.weight));
			int h = Integer.parseInt(Global.getConfig(Constant.ImageSeting.height));
			int iOldWidth = img.getWidth(null);
			int iOldHeight = img.getHeight(null);

			double w2 = (iOldWidth * 1.00) / (w * 1.00);
			double h2 = (iOldHeight * 1.00) / (h * 1.00);
			int INewWidth = (int) Math.round(iOldWidth / w2);
			int iNewHeight = (int) Math.round(iOldHeight / h2);

			BufferedImage bufImageForSave = new BufferedImage(INewWidth, iNewHeight, BufferedImage.TYPE_INT_RGB);
			bufImageForSave.getGraphics().drawImage(img.getScaledInstance(INewWidth, iNewHeight, Image.SCALE_SMOOTH),
					0, 0, null);

			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			inStream = file.getInputStream();

			byte[] buff = new byte[100];
			int rc = 0;
			while ((rc = inStream.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}

			ImageWriter imgWrier = ImageIO
					.getImageWritersByFormatName(Global.getConfig(Constant.ImageSeting.zipFormat)).next();
			ByteArrayOutputStream out = new ByteArrayOutputStream(swapStream.toByteArray().length);
			swapStream.close();

			imgWrier.reset();

			imgWrier.setOutput(ImageIO.createImageOutputStream(out));

			IIOMetadata imageMetaData = imgWrier.getDefaultImageMetadata(new ImageTypeSpecifier(bufImageForSave), null);
			Element tree = (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
			Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
			jfif.setAttribute("Xdensity", Integer.toString(iDpi));
			jfif.setAttribute("Ydensity", Integer.toString(iDpi));

			JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imgWrier.getDefaultWriteParam();
			jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
			jpegParams.setCompressionQuality(fCompressionQuality);

			imgWrier.write(imageMetaData, new IIOImage(bufImageForSave, null, null), null);

			out.flush();
			out.close();

			imgWrier.dispose();
			byte[] data = out.toByteArray();

			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 根据坐标以及需要剪裁图片的宽度和高度，剪裁上传文件，并按照设置好的比例进行压缩图片
	 * @param fileFullName 上传图片文件全路径
	 * @param imageType 压缩图片类型
	 * @param iX 剪裁在原图片的X坐标
	 * @param iY 剪裁在原图片的Y坐标
	 * @param iW 剪裁区域宽度（原图）
	 * @param iH 剪裁区域高度（原图）
	 * @return 图片字节数组
	 */
	public static byte[] shrinkPhotoByUploadFile(String fileFullName, String imageType, int iX, int iY, int iW, int iH) {
		FileInputStream is = null;
		ImageInputStream iis = null;
		ByteArrayOutputStream baoStream = null;
		try {
			is = new FileInputStream(fileFullName);
			
			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
			 * 参数：formatName - 包含非正式格式名称 . （例如 "jpeg" 或 "tiff"）等 。
			 */
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(imageType);
			ImageReader reader = it.next();
			
			// 获取图片流
			iis = ImageIO.createImageInputStream(is);
			
			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
			reader.setInput(iis, true);
			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
			 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
			 */
			ImageReadParam param = reader.getDefaultReadParam();
			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
			 */

			Rectangle rect = new Rectangle(iX, iY, iW, iH);
			// 提供一个 BufferedImage，将其用作解码像素数据的目标。
			param.setSourceRegion(rect);
			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
			 * BufferedImage 返回。
			 */
			BufferedImage bi = reader.read(0, param);

			int iDpi = 40;
			float fCompressionQuality = 0.1f;
			
			
			int iNewWidth = Integer.parseInt(Global.getConfig(Constant.ImageSeting.weight));
			int iNewHeight = Integer.parseInt(Global.getConfig(Constant.ImageSeting.height));
			
			BufferedImage bufImageForSave = new BufferedImage(iNewWidth, iNewHeight, BufferedImage.TYPE_INT_RGB); // 初始化新图片到缓存中
			// 将图片缩放到缓存中
			// Image.SCALE_SMOOTH 的缩略算法生成缩略图片的平滑度的优先级比速度高生成的图片质量比较好， 但速度慢
			bufImageForSave.getGraphics().drawImage(bi.getScaledInstance(iNewWidth, iNewHeight, Image.SCALE_SMOOTH), 0, 0, null);
			baoStream = new ByteArrayOutputStream();

			// 指定写图片的方式为 jpg
			ImageWriter imgWrier = ImageIO.getImageWritersByFormatName(imageType).next();
			imgWrier.reset();
			// 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何 OutputStream构造
			imgWrier.setOutput(ImageIO.createImageOutputStream(baoStream));

			// 取得包含与给定图像关联的元数据的 IIOMetadata 对象
			IIOMetadata imageMetaData = imgWrier.getDefaultImageMetadata(new ImageTypeSpecifier(bufImageForSave), null);
			Element tree = (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
			Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
			jfif.setAttribute("Xdensity", Integer.toString(iDpi));
			jfif.setAttribute("Ydensity", Integer.toString(iDpi));

			// 设定图片压缩质量
			JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imgWrier.getDefaultWriteParam();
			jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
			jpegParams.setCompressionQuality(fCompressionQuality);

			// 调用write方法，就可以向输入流写图片
			imgWrier.write(imageMetaData, new IIOImage(bufImageForSave, null, null), null);
			
			baoStream.flush();

			imgWrier.dispose();
			
			byte[] photoImg = baoStream.toByteArray();
			
	
			is.close();
			
			return photoImg;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baoStream != null) {
					baoStream.close();
				}
				if (iis != null) {
					iis.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * 根据坐标以及需要剪裁图片的宽度和高度，剪裁上传文件，并按照设置好的比例进行压缩图片
	 * @param iStream 输入文件流
	 * @param imageType 压缩图片类型
	 * @return 图片字节数组
	 */
	public static byte[] shrinkPhotoByUploadFileIO(InputStream iStream, String imageType) {
		
		ImageInputStream iis = null;
		ByteArrayOutputStream baoStream = null;
		
		try {
	
			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
			 * 参数：formatName - 包含非正式格式名称 . （例如 "jpeg" 或 "tiff"）等 。
			 */
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(imageType);
			ImageReader reader = it.next();
			
			// 获取图片流
			iis = ImageIO.createImageInputStream(iStream);
			
			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
			reader.setInput(iis, true);
			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
			 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
			 */
			ImageReadParam param = reader.getDefaultReadParam();
		
			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
			 * BufferedImage 返回。
			 */
			BufferedImage bi = reader.read(0, param);

			int iDpi = 40;
			float fCompressionQuality = 0.1f;
			
			
			int iNewWidth = bi.getWidth();
			int iNewHeight = bi.getHeight();
			
			BufferedImage bufImageForSave = new BufferedImage(iNewWidth, iNewHeight, BufferedImage.TYPE_INT_RGB); // 初始化新图片到缓存中
			// 将图片缩放到缓存中
			// Image.SCALE_SMOOTH 的缩略算法生成缩略图片的平滑度的优先级比速度高生成的图片质量比较好， 但速度慢
			bufImageForSave.getGraphics().drawImage(bi.getScaledInstance(iNewWidth, iNewHeight, Image.SCALE_SMOOTH), 0, 0, null);
			baoStream = new ByteArrayOutputStream();

			// 指定写图片的方式为 jpg
			ImageWriter imgWrier = ImageIO.getImageWritersByFormatName(imageType).next();
			imgWrier.reset();
			// 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何 OutputStream构造
			imgWrier.setOutput(ImageIO.createImageOutputStream(baoStream));

			// 取得包含与给定图像关联的元数据的 IIOMetadata 对象
			IIOMetadata imageMetaData = imgWrier.getDefaultImageMetadata(new ImageTypeSpecifier(bufImageForSave), null);
			Element tree = (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
			Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
			jfif.setAttribute("Xdensity", Integer.toString(iDpi));
			jfif.setAttribute("Ydensity", Integer.toString(iDpi));

			// 设定图片压缩质量
			JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imgWrier.getDefaultWriteParam();
			jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
			jpegParams.setCompressionQuality(fCompressionQuality);

			// 调用write方法，就可以向输入流写图片
			imgWrier.write(imageMetaData, new IIOImage(bufImageForSave, null, null), null);
			
			baoStream.flush();

			imgWrier.dispose();
			
			byte[] photoImg = baoStream.toByteArray();
			
	
			iStream.close();
			
			return photoImg;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baoStream != null) {
					baoStream.close();
				}
				if (iis != null) {
					iis.close();
				}
				if (iStream != null) {
					iStream.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return null;
	}
}
