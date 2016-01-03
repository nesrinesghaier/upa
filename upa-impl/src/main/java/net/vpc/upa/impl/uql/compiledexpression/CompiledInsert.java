package net.vpc.upa.impl.uql.compiledexpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.vpc.upa.impl.uql.DecObjectType;

public class CompiledInsert extends DefaultCompiledEntityStatement
        implements CompiledUpdateStatement, Cloneable {

    private static final long serialVersionUID = 1L;
    private List<CompiledVarVal> fields;
    private CompiledEntityName entity;

    public CompiledInsert() {
        fields = new ArrayList<CompiledVarVal>();
    }

    public CompiledInsert(String tabName, String[] fields, Object[] values) {
        this();
        into(tabName);
        for (int i = 0; i < fields.length; i++) {
            set(fields[i], values[i]);
        }

    }

    public CompiledInsert(CompiledInsert other) {
        this();
        addQuery(other);
    }

    private CompiledInsert into(String entity, String alias) {
        this.entity = new CompiledEntityName(entity);
        prepareChildren(this.entity);
        exportDeclaration(alias, DecObjectType.ENTITY, entity, alias);
        return this;
    }

    public CompiledInsert into(String entity) {
        return into(entity, null);
    }

    public CompiledEntityName getEntity() {
        return entity;
    }

    public int size() {
        return 3;
    }

    public CompiledInsert addQuery(CompiledInsert other) {
        if (other == null) {
            return this;
        }
        if (other.entity != null) {
            entity = (CompiledEntityName) other.entity.copy();
            exportDeclaration(null, DecObjectType.ENTITY, entity.getName(), null);
            prepareChildren(entity);
        }
        for (int i = 0; i < other.fields.size(); i++) {
            set(other.getField(i).getName(), other.getFieldValue(i).copy());
        }
        return this;
    }

    public DefaultCompiledExpression copy() {
        CompiledInsert o = new CompiledInsert();
        o.setDescription(getDescription());
        o.getClientParameters().setAll(getClientParameters());
        o.addQuery(this);
        return o;
    }

    public CompiledInsert set(String key, Object value) {
        if (value != null && (value instanceof DefaultCompiledExpression)) {
            return set(key, (DefaultCompiledExpression) value);
        } else {
            return set(key, ((DefaultCompiledExpression) (new CompiledLiteral(value, null))));
        }
    }

    public CompiledInsert set(String key, DefaultCompiledExpression value) {
        CompiledVar fName = new CompiledVar(key);
        CompiledVarVal varVal = new CompiledVarVal(fName, value);
        fields.add(varVal);
        prepareChildren(varVal);
        return this;
    }

    public CompiledInsert set(int index, DefaultCompiledExpression value) {
        if(index<fields.size()){
            CompiledVarVal varVal=fields.get(index);
            varVal.setSubExpression(1, value);
        }else{
            set(null, value);
        }
        return this;
    }

    public CompiledInsert set(String[] keys, Object[] values) {
        for (int i = 0; i < keys.length; i++) {
            set(keys[i], values[i]);
        }

        return this;
    }

    public CompiledInsert set(Map<String, Object> keysValues) {
        for (Map.Entry<String, Object> e : keysValues.entrySet()) {
            set(e.getKey(), e.getValue());
        }

        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Insert Into ");
        if (getEntity() != null) {
            sb.append(" ").append(getEntity());
        }
        sb.append("(");
        StringBuffer sbVals = new StringBuffer();
        boolean isFirst = true;
        for (CompiledVarVal v : fields) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(", ");
                sbVals.append(", ");
            }
            sb.append(v.getVar());
            sbVals.append(v.getVal());
        }

        return sb.append(") Values (").append(sbVals).append(")").toString();
    }

    public int countFields() {
        return fields.size();
    }

    public CompiledVar getField(int i) {
        return fields.get(i).getVar();
    }

    public DefaultCompiledExpression getFieldValue(int i) {
        return fields.get(i).getVal();
    }

    @Override
    public boolean isValid() {
        return entity != null && fields.size() > 0;
    }

    @Override
    public DefaultCompiledExpression[] getSubExpressions() {
        ArrayList<DefaultCompiledExpression> all = new ArrayList<DefaultCompiledExpression>();
        if (entity != null) {
            all.add(entity);
        }
        for (CompiledVarVal e : fields) {
            all.add(e);
        }
        return all.toArray(new DefaultCompiledExpression[all.size()]);
    }

    @Override
    public void setSubExpression(int index, DefaultCompiledExpression expression) {
        if (index == 0) {
            entity = (CompiledEntityName) expression;
            prepareChildren(expression);
        } else {
            fields.set(index-1, (CompiledVarVal)expression);
            prepareChildren(expression);
        }
    }

    protected List<CompiledNamedExpression> findEntityDefinitions() {
        ArrayList<CompiledNamedExpression> list = new ArrayList<CompiledNamedExpression>();
        list.add(new CompiledNamedExpression(null, getEntity()));
        return list;
    }
}
