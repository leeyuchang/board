package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.java.Log;

@CrossOrigin(origins = "*")
@Log
@Controller
public class SampleController {

    // public String index() {
    //     log.info("msg");
    //     return "petclinic";
    // }

    // @GetMapping("/sample1")
    // public void sample1(Model model) {
    //     model.addAttribute("greeting", "Hellow world");
    // }

    // @GetMapping("/sample5")
    // public void sample5(Model model) {
    //     log.info("/sample5");
    //     model.addAttribute("result", "SUCCESS");
    // }

    // @Resource(name = "accessTokenInfo")
    // AccessTokenInfo accessTokenInfo;

    // @GetMapping("/uploadpath")
    // public void uploadpath(Model model) {
    //     model.addAttribute("path", uploadPathService.getPath());
    //     model.addAttribute("accessTokenInfo", accessTokenInfo.getAccessToken());
    // }

    // @Autowired
    // FileUploadService uploadService;

    // @Autowired
    // UploadPathService uploadPathService;

    // @GetMapping(value = "/upload")
    // public void uploadGet() {
    // }

    // @PostMapping("/uploadpost")
    // public ResponseEntity<String> uploadpost(MultipartFile[] upfile)
    //         throws IOException, JsonSyntaxException, InterruptedException, ExecutionException, TimeoutException {

    //     Objects.requireNonNull(upfile, "NullPointerException");

    //     String result = "";

    //     for (MultipartFile m : upfile) {
    //         log.info("OriginalFilename:" + m.getOriginalFilename());
    //         log.info("ContentType:" + m.getContentType());
    //         log.info("Bytes:" + m.getBytes());
    //         log.info("Size:" + m.getSize());

    //         // result += uploadService.up(m.getContentType(), m.getBytes(), folderId, m.getOriginalFilename());

    //     }
    //     return new ResponseEntity<>(result, HttpStatus.OK);
    // }

    // @GetMapping("/image")
    // public void form() {
    // }

    // @GetMapping("/imageShow")
    // public ResponseEntity<byte[]> getThumbnailTest(){

    // HttpClient client1 = HttpClient.newHttpClient();

    // HttpRequest httpRequest = HttpRequest
    // .newBuilder(URI.create("https://drive.google.com/thumbnail?id=" +
    // "1XP1rW215GTkBlbmos7s53kF3g30folM9"))
    // .build();
    // HttpResponse<String> res = null;
    // try {
    // res = client1.send(httpRequest, HttpResponse.BodyHandlers.ofString());

    // Map<String, String> header = new HashMap<>();

    // for(Map.Entry<String, List<String>> entry: res.headers().map().entrySet() ) {
    // header.put(entry.getKey(),
    // entry.getValue().stream().collect(Collectors.joining(",")));
    // }

    // HttpRequest requst = HttpRequest.newBuilder()
    // .uri(URI.create(header.get("location")))
    // .header("content-type", "application/binary")
    // .header("set-cookie", header.get("set-cookie"))
    // .build();

    // HttpClient client2 = HttpClient.newHttpClient();

    // HttpResponse<byte[]> re = client2.send(requst,
    // HttpResponse.BodyHandlers.ofByteArray());
    // log.info(re.body().length + "");
    // return new ResponseEntity<>(re.body(), HttpStatus.OK);
    // } catch (IOException | InterruptedException e) {
    // e.printStackTrace();
    // log.info("Error");
    // return null;
    // }
    // }

    // @GetMapping("/imageShow2")
    // public ResponseEntity<byte[]> getThumbnailTest2() {

    // HttpClient client = HttpClient.newHttpClient();

    // HttpRequest request = HttpRequest
    // .newBuilder(URI.create("https://lh3.googleusercontent.com/d/1XP1rW215GTkBlbmos7s53kF3g30folM9=s220"))
    // .header("content-type", "application/binary").build();
    // try {
    // HttpResponse<byte[]> response = client.send(request,
    // HttpResponse.BodyHandlers.ofByteArray());
    // log.info(response.body().length + "");
    // return new ResponseEntity<>(response.body(), HttpStatus.OK);
    // } catch (IOException | InterruptedException e) {
    // e.printStackTrace();
    // log.info("Error");
    // return null;
    // }
    // }

    // HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
    //         .connectTimeout(Duration.parse("PT3S")).build();

    // @GetMapping("/imageShow3")
    // public ResponseEntity<byte[]> getThumbnailTest2()
    //         throws InterruptedException, ExecutionException, TimeoutException {

    //     HttpRequest request = HttpRequest
    //             .newBuilder(URI.create("https://lh3.googleusercontent.com/d/1XP1rW215GTkBlbmos7s53kF3g30folM9=s220"))
    //             .header("content-type", "application/binary")
    //             .build();

    //     CompletableFuture<HttpResponse<byte[]>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray());

    //     return new ResponseEntity<>(response.thenApply(HttpResponse::body).get(1,TimeUnit.SECONDS), HttpStatus.OK);
    // }

    @GetMapping("/sseForm")
    public void handleSse(){
        log.info("/sseForm");
    }

}