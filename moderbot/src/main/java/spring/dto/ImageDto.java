package spring.dto;

public class ImageDto {
    private Long id;
    private String title;
    private Byte[] content;
    
    public ImageDto(Long id, String title, Byte[] content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Byte[] getContent() {
        return content;
    }
    
    public void setContent(Byte[] content) {
        this.content = content;
    }
}