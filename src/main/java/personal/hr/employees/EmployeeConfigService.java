package personal.hr.employees;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import personal.UploadResponse;
import personal.security.Passport;
import personal.security.SessionUtils;

/**
 * Created by niko on 8/15/15.
 */
@RestController
@RequestMapping("/hr/employee/config")
public class EmployeeConfigService {
    @PersistenceContext
    private EntityManager em;

    @RequestMapping("/photo/change")
    @Transactional(rollbackFor = Throwable.class)
    public UploadResponse updatePhoto(@RequestParam Integer id,
                                      @RequestParam MultipartFile file) throws IOException {

        byte[] bytes = file.getBytes();
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        int  newWidth = (200 * width) / height;

        BufferedImage resizeImage = resizeImage(bufferedImage, newWidth, 200, 1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizeImage, "jpg", baos);

        Photo photo = new Photo();
        photo.id = id;
        photo.photo = baos.toByteArray();

        em.merge(photo);

        return new UploadResponse(true);
    }

    @RequestMapping("/photo/get")
    public ResponseEntity<byte[]> getPhoto(@RequestParam Integer id)
            throws IOException {

        Photo photo = em.find(Photo.class, id);

        ResponseEntity<byte[]> response;
        byte[] photoData;
        if (photo != null) {
            photoData = photo.photo;

        } else {

            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource("no_photo.jpg");

            File file;
            try {
                file = new File(url.toURI());
            } catch (URISyntaxException e) {
                file = new File(url.getPath());
            }

            photoData = FileUtils.readFileToByteArray(file);
        }

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Content-Type", "image/jpeg");

        response = new ResponseEntity<>(photoData, headers,
                HttpStatus.OK);

        return response;
    }



    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type) throws IOException {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    @RequestMapping("/appearance/change")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Boolean> changeAppearance(
            @RequestParam String appearance,
            HttpSession session){

        Passport passport = (Passport)session.getAttribute(SessionUtils.SESSION_DATA_KEY);
        Employee employee = em.find(Employee.class, passport.getEmployee().id);

        employee.appearance = appearance;

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @RequestMapping("/language/change")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Boolean> changeLanguage(
            @RequestParam String language,
            HttpSession session){

        Passport passport = (Passport)session.getAttribute(SessionUtils.SESSION_DATA_KEY);
        Employee employee = em.find(Employee.class, passport.getEmployee().id);

        employee.language = language;

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
