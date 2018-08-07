package org.learning.portal.service.support;

import org.apache.commons.lang3.StringUtils;
import org.learning.basic.core.domain.Pagination;
import org.learning.basic.core.domain.SearchForm;
import org.learning.basic.dao.IHibernateOperations;
import org.learning.basic.dao.support.SQLSupport.SQL;
import org.learning.portal.domain.Company;
import org.learning.portal.service.ICompanyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(ICompanyService.SERVICE_ID)
public class CompanyServiceImpl implements ICompanyService {

    @Autowired
    @Qualifier(IHibernateOperations.SERVICE_ID)
    private IHibernateOperations hibernateOperations;

    public Pagination<Company> listCompanys(SearchForm form) {
        SQL sql = new SQL();
        sql.append("from ").append(Company.class).append(" where 1 = 1");
        sql.appendIfExistForLike(" and p.name like ?", form.getKeywords());
        return hibernateOperations.findForPagination(sql.getSQL(), form.getOffset(), form.getLimit(), sql.getParams());
    }

    public Company getCompany(String id) {
        return hibernateOperations.get(Company.class, id);
    }

    @Transactional
    public void storeCompany(Company company) {
        Company companyToUse = null;
        if (StringUtils.isNotEmpty(company.getId())) {
            companyToUse = hibernateOperations.load(Company.class, company.getId());
        } else {
            companyToUse = company;
            companyToUse.setId(null);
        }

        BeanUtils.copyProperties(company, companyToUse, new String[] { "id" });

        hibernateOperations.saveOrUpdate(companyToUse);
    }

    @Transactional
    public void deleteCompany(String... ids) {
        String sql = "delete from " + Company.class.getName() + " where id in (";
        for (int i = 0; i < ids.length; i++) {
            sql += "?, ";
        }
        sql = StringUtils.removeEnd(sql, ", ");
        sql += ")";
        hibernateOperations.bulkUpdate(sql, (Object[]) ids);
    }

}
