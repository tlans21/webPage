package HomePage.controller.board;

//@Controller
//@RequestMapping("/board")
//public class boardController {
////    @Autowired
//    BoardService boardService;
//
//
//    // ====== read Board start line==============//
//    @GetMapping("/boardList")
//    public String getAllBoard(Model model){
//        List<Board> boards = boardService.
//        model.addAttribute("boards", boards);
//        return "/board/board";
//    }
//
//    @GetMapping("/{id}")
//    public String showBoardDetail(@PathVariable("id") Long id, Model model){
//        Board board = boardService.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + id));
//        model.addAttribute("board", board);
//        return "/board/board-detail";
//    }
//
//
//    // ====== readBoard end line==============//
//
//    // ====== createBoard start line==============//
//    @GetMapping("/create-board")
//    public String showCreateBoardPage(){
//        return "/board/createBoardPage";
//    }
//
//    @PostMapping("/create")
//    public RedirectView createBoard(@RequestParam String writer, @RequestBody String title, @RequestBody String content){
//        Board board = new Board();
//        board.setWriter(writer);
//        board.setTitle(title);
//        board.setContent(content);
//        System.out.println("1");
//        boardService.registerBoard(board);
//
//        return new RedirectView("/board/boardList"); // 게시글 작성 후 목록으로 리다이렉트
//    }
//
//    // ====== createBoard end line ==============//
//
//    @GetMapping("/communityBoardList")
//    public String getAllCommunityBoard(){
//        return "/board/communityBoardList.html";
//    }
//}
