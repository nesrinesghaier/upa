/**
 * ====================================================================
 * UPA (Unstructured Persistence API)
 * Yet another ORM Framework
 * ++++++++++++++++++++++++++++++++++
 * Unstructured Persistence API, referred to as UPA, is a genuine effort
 * to raise programming language frameworks managing relational data in
 * applications using Java Platform, Standard Edition and Java Platform,
 * Enterprise Edition and Dot Net Framework equally to the next level of
 * handling ORM for mutable data structures. UPA is intended to provide
 * a solid reflection mechanisms to the mapped data structures while
 * affording to make changes at runtime of those data structures.
 * Besides, UPA has learned considerably of the leading ORM
 * (JPA, Hibernate/NHibernate, MyBatis and Entity Framework to name a few)
 * failures to satisfy very common even known to be trivial requirement in
 * enterprise applications.
 * <p>
 * Copyright (C) 2014-2015 Taha BEN SALAH
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * ====================================================================
 */
package net.vpc.upa.expressions;

public class I2V extends FunctionExpression {

    private static final long serialVersionUID = 1L;
    private Expression expression;

    public I2V(Expression expression) {
        this.expression = expression;
    }

    public I2V(Expression[] expressions) {
        checkArgCount(getName(), expressions, 1);
        this.expression = expressions[0];
    }

    @Override
    public void setArgument(int index, Expression e) {
        if (index == 0) {
            this.expression = e;
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public Expression getExpression() {
        return expression;
    }

    //    public String toSQL(boolean integrated, PersistenceUnit database) {
//        return "i2v("+e.toSQL(true, database)+")";
//    }
//    public static Expression toExpression(Object value) {
//        return ((Expression) (value == null || !(value instanceof Expression) ? new I2V((String)(value)) : (Expression) value));
//    }
    @Override
    public String getName() {
        return "i2v";
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public Expression getArgument(int index) {
        if (index != 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return expression;
    }

    @Override
    public Expression copy() {
        I2V o = new I2V(expression.copy());
        return o;
    }
}
