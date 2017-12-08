package org.learning.portal.service;

import org.learning.basic.core.domain.Pagination;
import org.learning.basic.core.domain.SearchForm;
import org.learning.portal.domain.Company;

public interface ICompanyService {

    String SERVICE_ID = "portal.companyService";

    Pagination<Company> listCompanys(SearchForm form);

    Company getCompany(String id);

    void storeCompany(Company company);

    void deleteCompany(String... ids);
}
