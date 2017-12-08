package org.learning.basic.dao.support;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SQLSupport {

	public static final class SQL extends SQLSupport {

		private StringBuffer sql = new StringBuffer();
		private List<Object> params = new ArrayList<Object>();

		public SQL append(Class<?> clazz, Object... args) {
			return append(clazz.getName(), args);
		}

		public SQL append(String content, Object... args) {
			sql.append(content);
			for (int i = 0; args != null && i < args.length; i++) {
				params.add(args[i]);
			}
			return this;
		}

		public SQL addParams(String... args) {
			for (int i = 0; i < args.length; i++) {
				params.add(args[i]);
			}
			return this;
		}

		public SQL addParams(Collection<?> args) {
			params.addAll(args);
			return this;
		}

		public SQL addParams(Object... args) {
			for (int i = 0; i < args.length; i++) {
				params.add(args[i]);
			}
			return this;
		}

		public SQL addInParams(String... args) {
			for (int i = 0; i < ArrayUtils.getLength(args); i++) {
				sql.append("?");
				params.add(args[i]);

				if (i + 1 < args.length) {
					sql.append(", ");
				}
			}
			return this;
		}

		public SQL addInParams(Collection<?> args) {
			for (Iterator<?> it = args.iterator(); it.hasNext();) {
				sql.append("?");
				params.add(it.next());

				if (it.hasNext()) {
					sql.append(", ");
				}
			}
			return this;
		}

		public SQL addInParams(Object... args) {
			for (int i = 0; i < ArrayUtils.getLength(args); i++) {
				sql.append("?");
				params.add(args[i]);

				if (i + 1 < args.length) {
					sql.append(", ");
				}
			}
			return this;
		}

		public SQL appendIfExist(String content, Object... args) {
			if (exist(args)) {
				append(content, args);
			}
			return this;
		}

		public SQL appendIfExistForLike(String content, Object... args) {
			if (exist(args)) {
				Object[] argsToUse = new Object[args.length];
				for (int i = 0; i < args.length; i++) {
					argsToUse[i] = "%" + args[i] + "%";
				}
				append(content, argsToUse);
			}
			return this;
		}
		
		public SQL appendIfExistForLikeRight(String content, Object... args) {
			if (exist(args)) {
				Object[] argsToUse = new Object[args.length];
				for (int i = 0; i < args.length; i++) {
					argsToUse[i] = args[i] + "%";
				}
				append(content, argsToUse);
			}
			return this;
		}

		protected boolean exist(Object... args) {
			if (ArrayUtils.isEmpty(args)) {
				return false;
			}
			for (Object arg : args) {
				if (arg != null) {
					if (arg instanceof String) {
						if (StringUtils.isNotEmpty((String) arg)) {
							return true;
						}
					} else {
						return true;
					}
				}
			}
			return false;
		}

		public void removeEnd(int length) {
			sql.delete(sql.length() - length, sql.length());
		}

		public void removeEnd(String remove) {
			if (StringUtils.endsWith(sql.toString(), remove)) {
				removeEnd(remove.length());
			}
		}

		public boolean endsWith(String suffix) {
			return StringUtils.endsWith(sql.toString(), suffix);
		}

		public int indexOf(String text) {
			return sql.indexOf(text);
		}

		public String getSQL() {
			return sql.toString();
		}

		public Object[] getParams() {
			return params.toArray(new Object[params.size()]);
		}
	}
}
