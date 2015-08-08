package personal.hr.employees;

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
import personal.ListPage;
import personal.States;
import personal.UploadResponse;
import personal.hr.positions.Position;
import personal.hr.workplaces.Workplace;
import personal.utils.SqlUtils;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static personal.utils.SqlUtils.SqlStringContaining.NONE;
import static personal.utils.SqlUtils.SqlStringContaining.START;

@RestController
@RequestMapping("/hr/employee")
public class EmployeesService {

    @PersistenceContext
    private EntityManager em;

    public static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    @RequestMapping("/add")
    @Transactional(rollbackFor = Throwable.class)
    public Integer add(@RequestParam(required = false) String personalNo,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String birthDate,
            @RequestParam(required = false) Integer workplaceId,
            @RequestParam(required = false) Integer positionId)
                    throws ParseException {

        Employee employee = new Employee();
        employee.personalNo = personalNo;
        employee.email = email;
        employee.firstName = firstName;
        employee.lastName = lastName;
        employee.phone = phone;

        employee.positionId = positionId;
        if (positionId != null) {
            Position position = em.find(Position.class, positionId);
            employee.positionName = position.positionName;
        }

        employee.workplaceId = workplaceId;
        if (workplaceId != null) {
            Workplace workplace = em.find(Workplace.class, workplaceId);
            employee.workplaceName = workplace.workplaceName;
        }

        if (birthDate != null) {
            employee.birthDate = df.parse(birthDate);
        }

        em.persist(employee);

        return employee.id;
    }

    @RequestMapping("/update")
    @Transactional(rollbackFor = Throwable.class)
    public void update(@RequestParam Integer id,
            @RequestParam(required = false) String personalNo,
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String birthDate,
            @RequestParam(required = false) Integer workplaceId,
            @RequestParam(required = false) Integer positionId)
                    throws ParseException {

        Employee employee = em.find(Employee.class, id);
        employee.personalNo = personalNo;
        employee.email = email;
        employee.firstName = firstName;
        employee.lastName = lastName;
        employee.phone = phone;

        employee.positionId = positionId;
        if (positionId != null) {
            Position position = em.find(Position.class, positionId);
            employee.positionName = position.positionName;
        }

        employee.workplaceId = workplaceId;
        if (workplaceId != null) {
            Workplace workplace = em.find(Workplace.class, workplaceId);
            employee.workplaceName = workplace.workplaceName;
        }

        if (birthDate != null) {
            employee.birthDate = df.parse(birthDate);
        }

        em.merge(employee);
    }

    @RequestMapping("/enable")
    @Transactional(rollbackFor = Throwable.class)
    public void enable(@RequestParam Integer id) {

        Employee employee = em.find(Employee.class, id);
        employee.stateId = States.ACTIVE;
    }

    @RequestMapping("/disable")
    @Transactional(rollbackFor = Throwable.class)
    public void disable(@RequestParam Integer id) {

        Employee employee = em.find(Employee.class, id);
        employee.stateId = States.PENDING;
    }

    @RequestMapping("/remove")
    @Transactional(rollbackFor = Throwable.class)
    public void remove(@RequestParam Integer id) {

        Employee employee = em.find(Employee.class, id);
        employee.stateId = States.INACTIVE;
    }

    @RequestMapping("/get")
    public Employee get(@RequestParam Integer id) {

        return em.find(Employee.class, id);
    }

    @RequestMapping("/active")
    public List<Employee> getActive() {

        return em.createQuery(
                "from Employee where state =1 order by lastName,firstName",
                Employee.class).getResultList();
    }

    @RequestMapping("/search")
    public ListPage<Employee> search(
            @RequestParam(required = false) Integer state,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String personalNo,
            @RequestParam(required = false) Integer positionId,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer workplaceId,
            @RequestParam(required = false) String workplace) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();

        SqlUtils.sqlParam(firstName, params, sql,
                " and c.firstName like :firstName", START);

        SqlUtils.sqlParam(lastName, params, sql,
                " and c.lastName like :lastName", START);

        SqlUtils.sqlParam(workplace, params, sql,
                " and c.workplace like :workplace", START);

        SqlUtils.sqlParam(id, params, sql, " and c.id like :id");

        SqlUtils.sqlParam(workplaceId, params, sql,
                " and c.workplaceId like :workplaceId");

        SqlUtils.sqlParam(position, params, sql,
                " and c.position like :position", START);

        SqlUtils.sqlParam(positionId, params, sql,
                " and c.positionId like :positionId");

        SqlUtils.sqlParam(state, params, sql, " and c.stateId=:state");

        SqlUtils.sqlParam(personalNo, params, sql,
                " and c.personalNo = :personalNo", NONE);

        // Get count
        String cntText = "select count(c) from Employee c where 1=1 ";
        cntText = cntText.concat(sql.toString());
        TypedQuery<Long> cntQuery = em.createQuery(cntText, Long.class);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            cntQuery.setParameter(key, value);
        }
        int count = cntQuery.getSingleResult().intValue();

        // Get actual paged data.
        List<Employee> results;
        if (count == 0) {
            results = new ArrayList<Employee>();
        } else {

            String sqlText = "select c from Employee c where 1=1 ";
            sqlText = sqlText.concat(sql.toString());
            sqlText = sqlText.concat(" order by c.lastName,c.firstName");

            TypedQuery<Employee> sqlQuery;
            sqlQuery = em.createQuery(sqlText, Employee.class);

            for (Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                sqlQuery.setParameter(key, value);
            }

            results = sqlQuery.getResultList();
        }

        ListPage<Employee> resultList = new ListPage<Employee>();
        resultList.setList(results);
        resultList.setCount(count);

        return resultList;
    }

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
}