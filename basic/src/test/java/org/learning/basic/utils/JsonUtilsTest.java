package org.learning.basic.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.learning.basic.core.domain.Account;
import org.learning.basic.core.domain.Pagination;
import org.learning.basic.utils.JsonUtils.Jackson;
import org.learning.basic.utils.StatusUtils.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonUtilsTest {

    @Test
    public void test1() throws IOException {
        List<Account> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Account account = new Account();
            account.setId(String.valueOf(i));
            account.setName(String.valueOf(i));
            result.add(account);
        }
        Pagination<Account> pagination = new Pagination<>(0, 10, 100, result);
        Status<Pagination<Account>> status = new Status<>(true, "500", pagination);
        String content = Jackson.writeValueAsString(status);
        System.out.println(content);
        Object value = Jackson.readValue(content, new TypeReference<Status<Pagination<Account>>>() {
        });
        System.out.println(value);
//        IRestOperations restOperations = null;
//        SearchForm form = new SearchForm();
//        status = restOperations.postForObject("http://127.0.0.1:8080/api", form, new ParameterizedTypeReference<Status<Pagination<Account>>>() {
//        });
    }
}
