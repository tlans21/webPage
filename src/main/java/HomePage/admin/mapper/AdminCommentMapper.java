package HomePage.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface AdminCommentMapper {
    /*
        - comment의 boardId값을 통해 삭제
     * @Param 삭제해야할 boardId를 갖고있는 코멘트들
     * @return 삭제된 코멘트 갯수
     */
    int deleteAllByBoardId(@Param("boardId") Long boardId);

}
