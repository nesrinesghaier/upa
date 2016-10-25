package net.vpc.upa;

/**
 * Created by vpc on 6/2/16.
 */
public class NamedId {
    private Object id;
    /**
     * name is changed from String to Object as some Entities does not
     * define String typed @Main fields.
     */
    private Object name;

    public NamedId() {
    }

    public NamedId(Object id, Object name) {
        this.id = id;
        this.name = name;
    }

    public Object getId() {
        return id;
    }

    public Object getName() {
        return name;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public void setName(Object name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedId)) return false;

        NamedId namedId = (NamedId) o;

        if (id != null ? !id.equals(namedId.id) : namedId.id != null) return false;
        return !(name != null ? !name.equals(namedId.name) : namedId.name != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
