package com.molla.helper;

import com.molla.model.Product;
import com.molla.model.ProductImage;
import com.molla.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ProductSaveHelper {

    public static void  saveUploadedImages(MultipartFile mainImageMultipart,
                                           MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {

        log.debug("ProductSaveHelper | saveUploadedImages is started");

        log.debug("ProductSaveHelper | setMainImageName | !mainImageMultipart.isEmpty() : " + !mainImageMultipart.isEmpty());

        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());

            log.debug("ProductSaveHelper | setMainImageName | fileName : " + fileName);

            String uploadDir = "product-images/" + savedProduct.getId();

            log.debug("ProductSaveHelper | setMainImageName | uploadDir : " + uploadDir);

            FileUploadUtil.cleanDir(uploadDir);

            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        log.debug("ProductSaveHelper | setMainImageName | extraImageMultiparts.length : " + extraImageMultiparts.length);

        if (extraImageMultiparts.length > 0) {

            String uploadDir = "product-images/" + savedProduct.getId() + "/extras";

            log.debug("ProductSaveHelper | setMainImageName | uploadDir : " + uploadDir);

            for (MultipartFile multipartFile : extraImageMultiparts) {

                log.debug("ProductController | setMainImageName | multipartFile.isEmpty() : " + multipartFile.isEmpty());
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                log.debug("ProductSaveHelper | setMainImageName | fileName : " + fileName);

                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            }
        }


        log.debug("ProductSaveHelper | saveUploadedImages is completed");
    }


    public static void deleteExtraImagesWeredRemovedOnForm(Product product) {

        log.debug("ProductSaveHelper | deleteExtraImagesWeredRemovedOnForm is started");

        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);

        log.debug("ProductSaveHelper | deleteExtraImagesWeredRemovedOnForm | dirPath  : " + dirPath);


        try {
            Files.list(dirPath).forEach(file -> {
                String filename = file.toFile().getName();

                if (!product.containsImageName(filename)) {
                    try {
                        Files.delete(file);
                        log.debug("Deleted extra image: " + filename);

                    } catch (IOException e) {
                        log.debug("Could not delete extra image: " + filename);
                    }
                }

            });
        } catch (IOException ex) {
            log.debug("Could not list directory: " + dirPath);
        }
    }

    public static void  setExistingExtraImageNames(String[] imageIDs, String[] imageNames,
                                                   Product product) {

        log.debug("ProductSaveHelper | setExistingExtraImageNames is started");

        if (imageIDs == null || imageIDs.length == 0) return;

        log.debug("ProductSaveHelper | deleteExtraImagesWeredRemovedOnForm | imageIDs  : " + imageIDs.toString());
        log.debug("ProductSaveHelper | deleteExtraImagesWeredRemovedOnForm | imageNames  : " + imageNames.toString());

        Set<ProductImage> images = new HashSet<>();

        for (int count = 0; count < imageIDs.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];

            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);

    }

    public static void  setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {

        log.debug("ProductSaveHelper | setNewExtraImageNames is started");

        log.debug("ProductSaveHelper | setNewExtraImageNames | extraImageMultiparts.length : " + extraImageMultiparts.length);

        if (extraImageMultiparts.length > 0) {

            for (MultipartFile multipartFile : extraImageMultiparts) {

                log.debug("ProductSaveHelper | setNewExtraImageNames | !multipartFile.isEmpty() : " + !multipartFile.isEmpty());

                if (!multipartFile.isEmpty()) {

                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                    log.debug("ProductSaveHelper | setNewExtraImageNames | fileName : " + fileName);


                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }


                }
            }
        }

        log.debug("ProductSaveHelper | setExtraImageNames is completed");
    }

    public static void  setMainImageName(MultipartFile mainImageMultipart, Product product) {

        log.debug("ProductSaveHelper | setMainImageName is started");

        log.debug("ProductSaveHelper | setMainImageName | !mainImageMultipart.isEmpty() : " + !mainImageMultipart.isEmpty());

        if (!mainImageMultipart.isEmpty()) {


            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());

            log.debug("ProductSaveHelper | setMainImageName | fileName : " + fileName);

            product.setMainImage(fileName);

        }


        log.debug("ProductSaveHelper | setMainImageName is completed");
    }


    public static void  setProductDetails(String[] detailIDs, String[] detailNames,
                                          String[] detailValues, Product product) {

        log.debug("ProductSaveHelper | setProductDetails is started");

        log.debug("ProductSaveHelper | setProductDetails | detailNames : " + detailNames.toString());
        log.debug("ProductSaveHelper | setProductDetails | detailNames : " + detailValues.toString());
        log.debug("ProductSaveHelper | setProductDetails | product : " + product.toString());


        if (detailNames == null || detailNames.length == 0) return;

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];
            Integer id = Integer.parseInt(detailIDs[count]);

            if (id != 0) {
                product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }

        log.debug("ProductSaveHelper | setProductDetails | product with its detail : " + product.getDetails().toString());

        log.debug("ProductSaveHelper | setProductDetails is completed");
    }

}
